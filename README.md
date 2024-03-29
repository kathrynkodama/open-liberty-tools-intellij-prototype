# Open Liberty Tools IntelliJ Plugin (Prototype)

This extension will detect Liberty Maven or Liberty Gradle projects in your
workspace.  Through the Liberty Dev Dashboard, you can start,
stop, or interact with Liberty dev mode on all available 
[Liberty Maven](https://github.com/OpenLiberty/ci.maven/blob/master/docs/dev.md#dev) 
and [Liberty Gradle](https://github.com/OpenLiberty/ci.gradle/blob/master/docs/libertyDev.md) projects in your workspace.

Note that this extension requires the [Integrated Terminal plugin](https://plugins.jetbrains.com/plugin/13123-terminal) to be enabled.


## Developing
Developing this extension using the built-in [gradle-intellij-plugin](https://github.com/JetBrains/gradle-intellij-plugin/).

1. Clone this repository: `git clone git@github.com:kathrynkodama/open-liberty-tools-intellij.git`
2. Import this repository as a gradle project in IntelliJ IDEA
3. Run `./gradlew buildPlugin` to build a `.zip` that can be imported as gradle plugin or execute the following Gradle task to build and execute an IntelliJ instance:
`./gradlew runIde`


## To Implement
- [ ] Maven Support
    - [x] Detect whether `io.openliberty.tools:liberty-maven-plugin` is in the `pom.xml` (in build tag or profiles tag)
    - [x] Start dev mode
    - [x] Stop dev mode
    - [x] Custom start dev mode
    - [x] Run tests
    - [x] View integration test report (opens in a browser using `BrowserUtil.browse(...)`)
    - [x] View unit test report (opens in a browser using `BrowserUtil.browse(...)`)
    - [x] View Effective Pom
    - [ ] Support multi-module projects
    
- [ ] Gradle Support
    - [x] Detect whether `io.openliberty.tools:liberty-maven-plugin` is in the `build.gradle` (with File IO and regex. 
    NOTE: comments in the `build.gradle` are not handled yet) 
    - [x] Start dev mode
    - [x] Stop dev mode
    - [x] Custom start dev mode
    - [x] Run tests
    - [x] View test report
    - [x] View Gradle Config
    - [ ] Support multi-module projects

- Action Bar in Liberty Dev Tool Window
    - [x] Refresh button
    - [ ] Execute selected dev task button

- Misc.
    - [ ] Validate user inputted custom start parameters
    - [ ] Display a warning message if user selects `Stop dev mode` or `Run tests` and if terminal session does not exist
    - [ ] Implement proper logging
    - [ ] Setting java home environment variable (this is done in the VS Code extension, investigate if there is something similar in intelliJ)
    - [ ] Start... to provide selectable options (see https://github.com/OpenLiberty/liberty-dev-vscode-ext/issues/60)
    
