# MountainPenguins

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

## User Manual

## Developer Guidelines

### Basic Guidelines for GitHub Use
- Appropriate issue management:
  - When creating a new issue, add a brief description and appropriate labels so that the team knows what the issue is meant to accomplish
  - Assign yourself to an issue when you begin work on that, so that the team always has an overview ower that is "In progress" and what is not
  - Make sure to close issues that have been completed or are no longer relevant
- Create a unique branch for each issue, branching out of the dev branch. Code should never be pushed directly into main
- Prefix branch-names with the appropriate "label" from the list below (ex. feat/add-firebase)
  - feat: new feature or enhancement of existing feature
  - fix: bug fix
  - docs: adding documentation
  - chore: minor changes that do not fit into any other category
- Regularly push your progress to the relevant branch, even if some of it might still be WIP. This helps the team keep track of progress and makes it easier to flexibly distribute tasks to other members of the team
- Each pull request needs the approval of at least one other reviewer before being merged into the dev branch
  
