# MangaBubble Translate (prototype Android)

Application Android native Kotlin destinée à traduire des pages de manga/manhwa de l'anglais vers le français.

## Fonctionnalités présentes dans ce prototype
- Import d'une image depuis le téléphone.
- OCR ML Kit pour texte latin, configuré pour les dialogues en anglais.
- Traduction anglais → français avec les modèles ML Kit téléchargeables.
- Bulle flottante Android qui ouvre l'application (appui long pour la fermer).
- Interface simple et historique du dernier résultat pendant la session.

## Limites importantes
Ce projet est un **prototype source**, pas encore un clone complet de Ballon Translator.
La bulle ne capture pas encore l'écran : la capture inter-applications nécessite un flux `MediaProjection`
avec consentement système, puis une pipeline OCR/traduction adaptée. Le remplacement du texte dans
les bulles et la reconstruction des arrière-plans ne sont pas encore implémentés.
La traduction ML Kit télécharge les modèles au premier usage : le téléchargement initial nécessite
Internet. Ensuite, les modèles téléchargés peuvent fonctionner hors ligne pour les langues prises en charge.
Les résultats OCR et de traduction varient selon la qualité de l'image et le modèle.

## Ouvrir et compiler
1. Installer Android Studio.
2. Ouvrir le dossier `MangaBubbleTranslate`.
3. Laisser Gradle synchroniser les dépendances.
4. Compiler puis exécuter sur un appareil Android.
5. Pour la bulle, appuyer sur « Activer la bulle flottante » et autoriser l'affichage par-dessus les autres applications.

## Versions
- Android Studio récent
- JDK 17
- Android SDK 35
- Minimum Android 6.0 (API 23)

## Confidentialité
L'OCR et la traduction d'image sont traités sur l'appareil par ML Kit. Le modèle de traduction est téléchargé
depuis Google lors de sa première utilisation. L'application ne contient pas de compte ni d'analytique.

## Développement depuis Android avec Termux
Cette archive contient les sources du projet Android natif, mais pas un APK compilé.
Sur téléphone, la compilation Android/Gradle peut être compliquée et gourmande en mémoire.
Méthode recommandée :
1. Installer Termux depuis une source officielle comme F-Droid ou GitHub.
2. Installer Git, unzip, OpenJDK et les outils de base avec le gestionnaire de paquets Termux.
3. Pour compiler un projet Android natif complet, il faut aussi un Android SDK compatible et Gradle configuré ; l'installation du SDK complet sur téléphone peut demander beaucoup d'espace.
4. Alternative plus simple : utiliser un environnement de compilation distant fiable ou Android Studio sur ordinateur pour générer l'APK, puis le transférer sur le téléphone.

Le code actuel est réglé par défaut sur **anglais → français**. La capture d'écran d'autres applications et le remplacement visuel des bulles ne sont pas encore implémentés.


## Générer l'APK sans installer Termux (depuis un téléphone)
Le dépôt inclut `.github/workflows/build-apk.yml` pour compiler le projet sur les serveurs GitHub Actions.

1. Dans le navigateur du téléphone, connecte-toi à GitHub et crée un dépôt public nommé `MangaBubbleTranslate`.
2. Décompresse cette archive avec le gestionnaire de fichiers Android.
3. Sur la page du dépôt GitHub, utilise **Add file → Upload files** pour envoyer les fichiers et dossiers extraits (y compris `.github`; affiche les fichiers cachés si nécessaire), puis valide avec **Commit changes**.
4. Ouvre l'onglet **Actions** du dépôt et autorise les workflows si GitHub le demande.
5. Ouvre l'exécution **Build Android APK** et attends qu'elle termine.
6. Dans cette exécution, descends à **Artifacts** et télécharge `MangaBubbleTranslate-debug-apk`.
7. Décompresse l'artefact ZIP téléchargé pour récupérer `app-debug.apk`, puis ouvre-le pour installer. Android peut demander l'autorisation d'installation depuis le navigateur ou le gestionnaire de fichiers.

Si le navigateur mobile ne permet pas d'envoyer correctement les dossiers, il faudra utiliser un service de développement cloud ou un ordinateur. N'installe un APK que si tu connais sa provenance.
