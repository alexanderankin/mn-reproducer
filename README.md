# Fix

the fix is to pre-load conflicting libraries in the root project's `build.gradle` (courtesy of [vampire](https://github.com/Vampire)):

```groovy
plugins {
    id 'io.micronaut.application'        version '3.6.6' apply false
    id 'io.micronaut.library'            version '3.6.6' apply false
    id 'com.github.johnrengelman.shadow' version '7.1.2' apply false
}
```

# mn plugin issue reproducer

this repo is a reproducer for these two issues:

    https://github.com/graalvm/native-build-tools/issues/70

    https://github.com/gradle/gradle/issues/17559

for some reason i am running into these and intellij cannot load the tasks for those two projects (`micronaut-form-login{,-test}`)

simply after adding the micronaut library/application plugins
