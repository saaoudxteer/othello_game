# Othello - Groupe 33

Ce projet est une implémentation complète du jeu de stratégie **Othello** développée en Java avec l'interface graphique JavaFX.

## Membres du groupe

  * **RODRIGUES Gabriel**
  * **SAAOUDI Mohamed**
  * **RAETH Léandre**
  * **LAYACHI Nassime**
  * **ESSO Kevin**

-----

## Prérequis techniques

Pour exécuter ce projet, vous devez disposer de :

  **Java JDK 25** installé sur votre machine.

Vérifiez votre version de Java avec la commande :
```bash
java -version
```
Si votre version est trop antérieure, voir la Méthode 2 directement.

-----

## Installation et Exécution

### Méthode 1 : Lancement rapide (Exécutable fourni)

Un fichier exécutable **`othello.jar`** a été placé à la racine du projet pour faciliter le lancement. Vous n'avez pas besoin de compiler le code.

**Commande à exécuter à la racine du projet :**
```bash
java -jar othello.jar
```
-----

### Méthode 2 : Compilation via Gradle (Depuis les sources)

Si vous souhaitez recompiler le projet vous-même, utilisez le **Gradle Wrapper** fourni.

#### A. Lancer directement le jeu

Cette commande compile et exécute l'application en une seule étape.

  * **Windows :**
    ```cmd
    ./gradlew.bat run
    ```
  * **Linux / macOS :**
    ```bash
    ./gradlew run
    ```

*(Note pour Linux/Mac : Si vous avez une erreur "Permission denied", rendez le script exécutable avec `chmod +x gradlew`)*
