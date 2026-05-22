## The Fall of Shurima
SENG201 Team 76

## Team Members:
- Mohammed Shoeb Daniyal - moh28
- Xinyi Zhang - xzh250


## How to Run the Game:

1. Open the project in IntelliJ IDEA.
2. Wait for Gradle to load the project.
3. Run the main class:

   seng201.team76.App

The game should open from the start screen.

## How to Run from Terminal:

1. Linux / macOS: ./gradlew run

2. Windows: .\gradlew.bat run

## How to Run Tests:

1. Linux / macOS: ./gradlew test

2. Windows: .\gradlew.bat test

## Game Summary:

The Fall of Shurima is a JavaFX strategy/RPG game. 
The player chooses Aatrox or Xolaani as the main character, builds a guild, 
makes quest decisions, manages loyalty and madness, buys potions, and fights bosses.

The game includes:

- Guild setup
- Character selection
- Quest map
- Story events
- Boss fights
- Memory-game attack system
- Potion healing
- Permanent death and abandonment
- Final victory or defeat screen


## Basic Controls:

The game is controlled with the mouse.

- Click buttons to move between screens.
- Click quest buttons to start quests.
- Click story choices during events.
- Click memory-game images in the correct order during battle.
- Use Attack, Heal, Change Member, and Special Move during boss fights.


## Testing:

## Unit tests focus on the model classes because the models contain the main game rules.

## Tests cover:

- Adventurer loyalty and madness
- Guild party rules
- Gold and recruitment
- Potion healing
- Memory game logic
- Boss fight mechanics
- Quest progression
- Final battle behaviour

## JavaFX screens and controllers were tested manually by playing through the game.


## Image and Resource Notes:

All image files must be placed in: src/main/resources/images/

FXML files must be placed in: src/main/resources/fxml/

CSS files must be placed in: src/main/resources/css/


## Known Issues:

- The game depends on correct image names and resource paths.
- Some artwork may be improved later.
- GUI behaviour is manually tested rather than tested with automated JavaFX tests.


## Riot Games Disclaimer:

This is a non-commercial student project created for SENG201 coursework.
The project is inspired by Riot Games' League of Legends / Runeterra universe.
Riot Games does not endorse, sponsor, or approve this project.
Riot Games, League of Legends, Runeterra, and related characters are trademarks or intellectual property of Riot Games.
This project is for educational use only and is not intended for sale or public commercial release.


## Submission Checklist:

Before submitting, make sure:
- The project runs from IntelliJ.
- The tests pass.
- The JAR builds successfully.
- The JAR runs on the Linux Mint lab machines.
- All required images are included.
- The report is exported as PDF.
- UML diagrams are included.
- The final code is pushed to Eng-Git.
