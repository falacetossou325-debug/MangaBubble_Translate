package com.mangabubble.translate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.nl.translate.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var resultText: TextView
    private lateinit var statusText: TextView
    private lateinit var languageSpinner: Spinner
    // Source par défaut : anglais, cible fixe : français.
    private val languages = listOf("Anglais" to TranslateLanguage.ENGLISH)
    private var translator: Translator? = null
    private var sourceText = ""

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) recognizeImage(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    private fun buildUi() {
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(0xFF11111B.toInt())
        }
        val title = TextView(this).apply {
            text = "MangaBubble Translate"
            textSize = 25f
            setTextColor(0xFFFFFFFF.toInt())
        }
        val subtitle = TextView(this).apply {
            text = "Anglais → Français • OCR + traduction locale après téléchargement"
            textSize = 14f
            setTextColor(0xFFCBC6DA.toInt())
            setPadding(0, 8, 0, 18)
        }
        languageSpinner = Spinner(this)
        languageSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            languages.map { it.first })
        languageSpinner.isEnabled = false
        val choose = Button(this).apply {
            text = "Importer une page (anglais → français)"
            setOnClickListener { pickImage.launch("image/*") }
        }
        val overlay = Button(this).apply {
            text = "Activer la bulle flottante"
            setOnClickListener { startOverlay() }
        }
        statusText = TextView(this).apply {
            text = "Prêt. Choisis une image pour commencer."
            setTextColor(0xFFB9E6D3.toInt())
            setPadding(0, 12, 0, 12)
        }
        resultText = TextView(this).apply {
            text = "Le texte reconnu et sa traduction apparaîtront ici."
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            setTextIsSelectable(true)
        }
        val scroll = ScrollView(this).apply { addView(resultText) }
        outer.addView(title)
        outer.addView(subtitle)
        outer.addView(languageSpinner)
        outer.addView(choose)
        outer.addView(overlay)
        outer.addView(statusText)
        outer.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(outer)
    }

    private fun recognizeImage(uri: Uri) {
        statusText.text = "Analyse de l'image…"
        try {
            val image = InputImage.fromFilePath(this, uri)
            // Cette version vise les pages où les dialogues sont en anglais.
            val recognizer = TextRecognition.getClient(
                com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS
            )
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    sourceText = visionText.text
                    if (sourceText.isBlank()) {
                        statusText.text = "Aucun texte détecté. Essaie une image plus nette."
                    } else {
                        resultText.text = "TEXTE RECONNU\n\n$sourceText"
                        translate(sourceText)
                    }
                    recognizer.close()
                }
                .addOnFailureListener {
                    statusText.text = "Erreur OCR : ${it.localizedMessage ?: "analyse impossible"}"
                    recognizer.close()
                }
        } catch (e: Exception) {
            statusText.text = "Impossible d'ouvrir l'image : ${e.localizedMessage}"
        }
    }

    private fun translate(text: String) {
        val source = TranslateLanguage.ENGLISH
        statusText.text = "Préparation du modèle de traduction…"
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(source)
            .setTargetLanguage(TranslateLanguage.FRENCH)
            .build()
        translator?.close()
        translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()
        translator!!.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                statusText.text = "Traduction…"
                translator!!.translate(text)
                    .addOnSuccessListener { translated ->
                        resultText.text = "TEXTE ORIGINAL\n\n$text\n\nTRADUCTION FRANÇAISE\n\n$translated"
                        statusText.text = "Terminé. Modèle disponible pour les prochaines utilisations."
                    }
                    .addOnFailureListener { statusText.text = "Erreur de traduction : ${it.localizedMessage}" }
            }
            .addOnFailureListener {
                statusText.text = "Téléchargement du modèle impossible. Vérifie la connexion puis réessaie."
            }
    }

    private fun startOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivity(intent)
            Toast.makeText(this, "Autorise l'affichage par-dessus les autres applications, puis reviens ici.", Toast.LENGTH_LONG).show()
        } else {
            startService(Intent(this, FloatingBubbleService::class.java))
            Toast.makeText(this, "Bulle activée. Appuie dessus pour ouvrir l'application.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        translator?.close()
        super.onDestroy()
    }
}
