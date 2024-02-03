![CI](https://github.com/aleris/tscfg-plugin-gradle/actions/workflows/ci.yaml/badge.svg)

# Typesafe Config Gradle Plugin

Gradle plugin for generating [Typesafe Config](https://github.com/lightbend/config) files and associated Java classes 
using [tscfg](https://github.com/carueda/tscfg).


## Motivation

Lightbend typesafe config HOCON (Human-Optimized Config Object Notation) is a superset of JSON that is used for 
configuration files. Is one of the most popular configuration libraries because it is reliable, and powerful.

The tscfg library provides a way to generate a typesafe config file and associated Java classes from a template
spec file. For more details, see the [tscfg readme](https://github.com/carueda/tscfg).

This plugin wraps the tscfg library to provide a simple way to automatically generate the config files 
and configuration POJO classes with Gradle.

For the moment, only Java config classes are supported.

## Usage

Basic configuration:

Kotlin DSL:

```kts
plugins {
  id("java")
  id("io.github.aleris.plugins.tscfg") version "0.1.0"
}

tscfg {
    packageName = "com.example"

    files {
        register("src/tscfg/application.spec.conf") {
            outputConfigFileName = "src/main/resources/application.conf"
        }
    }

    generateRecords = true
    addGeneratedAnnotation = true
}
```

Groovy DSL:

```groovy
plugins {
    id 'java'
    id 'io.github.aleris.plugins.tscfg' version '0.1.0'
}

tscfg {
    packageName = 'io.github.aleris.plugins.tscfg.example'

    files {
        'src/tscfg/application.spec.conf' {
            outputConfigFileName = 'src/main/resources/application.conf'
        }
    }

    generateRecords = true
    addGeneratedAnnotation = true
}
```

This will generate a `src/main/resources/application.conf` file and a `com.example.ApplicationConfig` class
in `generated/sources` source set that can be used to load the config.

```java
Config config = ConfigFactory.load().resolve();
ApplicationConfig applicationConfig = new ApplicationConfig(config);
```
