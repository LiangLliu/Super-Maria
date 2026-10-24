# Super-Maria

* This project is cloned from [ahmetcandiroglu/Super-Mario-Bros](https://github.com/ahmetcandiroglu/Super-Mario-Bros).

* Classic Super Mario Bros. game implemented with Java.

## Requirements

* JDK 25 - the Gradle toolchain compiles and runs the game with it.

## Run

```shell
./gradlew run --quiet
```

## Test

```shell
./gradlew test
```

## Project layout

| Package | Responsibility |
|---|---|
| `model` | Entities and their rules; depends only on `config` |
| `loader` | Map/sprite/audio resources; turns a map PNG into a `Map` |
| `manager` | Map lifecycle, collision rules, input actions, sound |
| `engine` | Game loop, frame pacing, state machine, `GameContext` implementation |
| `view` | Swing panel, HUD and menu screens |
| `config`, `core`, `async` | Constants, camera/selection helpers, virtual-thread helpers |

Dependency rule: `model` never imports `engine`, `manager`, `view` or `loader`.

## About game

You can visit [wikipedia page](https://en.wikipedia.org/wiki/Super_Mario_Bros.) or [Super Mario wiki page](https://www.mariowiki.com/Super_Mario_Bros.) for detailed information about the game.

## Built With
* [Java](https://www.java.com/)

## In-game Screens

### Start Screen
![start screen](https://raw.githubusercontent.com/ahmetcandiroglu/1G.Super-Mario-Bros/master/docs/Screenshots/Start%20screen.png)

### Inside Game
![in game screen](https://raw.githubusercontent.com/ahmetcandiroglu/1G.Super-Mario-Bros/master/docs/Screenshots/In%20game%20screen.png)

### Pause Screen
![pause screen](https://raw.githubusercontent.com/ahmetcandiroglu/1G.Super-Mario-Bros/master/docs/Screenshots/Pause%20screen.png)