[![CircleCI](https://circleci.com/gh/circleci/circleci-docs.svg?style=svg)](https://github.com/kosenda/AutoSizeTable)　[![Renovate](https://img.shields.io/badge/renovate-enabled-brightgreen.svg?style=flat)](https://renovatebot.com)　[![](https://jitpack.io/v/kosenda/AutoSizeTable.svg)](https://jitpack.io/#kosenda/AutoSizeTable)

# AutoSizeTable
Jetpack Compose library to easily create tables with each item resized。


> [!WARNING]
> **Poor performance**

## ▪ Sample
https://github.com/kosenda/AutoSizeTable/assets/60963155/b9804c80-dc58-4792-b055-56008ce803de

https://github.com/kosenda/AutoSizeTable/blob/99719f4841fab6b03629a178bfdf712bfdfcdd4f/app/src/main/java/ksnd/autosizetable/SampleScreen.kt#L32-L141



## ▪ How to setup

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
