[![CircleCI](https://circleci.com/gh/circleci/circleci-docs.svg?style=svg)](https://github.com/kosenda/AutoSizeTable)[![Renovate](https://img.shields.io/badge/renovate-enabled-brightgreen.svg?style=flat)](https://renovatebot.com)[![](https://jitpack.io/v/kosenda/AutoSizeTable.svg)](https://jitpack.io/#kosenda/AutoSizeTable)

# AutoSizeTable

Jetpack Compose library to easily create tables with each item resized。


> [!WARNING]
> **Poor performance**

## ▪ Sample

https://github.com/user-attachments/assets/0fa91f31-424a-46ad-a224-83201041e2a0

https://github.com/kosenda/AutoSizeTable/blob/dc28402d64bf1f349f3834aae9658b89ab9d4283/app/src/main/java/ksnd/autosizetable/SampleScreen.kt#L31-L120

## ▪ How to setup

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
  }
}
```

### Step 2. Add the dependency

See [release page](https://github.com/kosenda/AutoSizeTable/releases) for `<version>`

```
dependencies {
  implementation("com.github.kosenda:AutoSizeTable:<version>")
}
```

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=kosenda/AutoSizeTable&type=Date)](https://www.star-history.com/#kosenda/AutoSizeTable&Date)
