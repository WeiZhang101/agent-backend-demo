## Task: Set up and configure git hooks to check the code before push the code

### Objective

Set up and configure git hooks to check the code before push the code

### Required Files to Create

1. `gradle/githooks/git-hooks.gradle`: install pre-push git hooks
2. `gradle/githooks/pre-push`: script to execute the `./gradlew clean build test`

### Configuration Requirements

#### 1. Gradle Configuration

- Define one new task named: `installGitHooks` in `gradle/githooks/git-hooks.gradle`, copy the `gradle/githooks/pre-push` into '.git/hooks' folder
- installGitHooks must be executed before compileJava

#### 2. Additional requirements

- Ensure the `installGitHooks` task is compatible with configuration cache
- Use ExecOperations.exec(Action) or ProviderFactory.exec(Action) instead. Don't use method exec(Closure) because it has been deprecated.
