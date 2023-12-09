# 🏗️🏗️🏗️　 In progress...　🏗️🏗️🏗️

[![CircleCI](https://circleci.com/gh/circleci/circleci-docs.svg?style=svg)](https://github.com/kosenda/AutoSizeTable)　[![Renovate](https://img.shields.io/badge/renovate-enabled-brightgreen.svg?style=flat)](https://renovatebot.com)　[![](https://jitpack.io/v/kosenda/AutoSizeTable.svg)](https://jitpack.io/#kosenda/AutoSizeTable)

# AutoSizeTable
This library can be used with Jetpack Compose to easily create tables that will resize.

> [!WARNING]
> **Poor performance**




# How to setup

### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}
```

### Step 2. Add the dependency 
See [release page](https://github.com/kosenda/AutoSizeTable/releases) for `<version>`
```
dependencies {
  implementation 'com.github.kosenda:AutoSizeTable:<version>
}
```
