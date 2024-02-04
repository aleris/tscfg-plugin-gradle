![CI](https://github.com/aleris/tscfg-plugin-gradle/actions/workflows/ci.yaml/badge.svg)

# Typesafe Config Gradle Plugin

Gradle plugin for generating [Typesafe Config](https://github.com/lightbend/config) files and associated Java classes 
using [tscfg](https://github.com/carueda/tscfg).


## Motivation

Lightbend typesafe config [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md) 
(Human-Optimized Config Object Notation) is a superset of JSON that is used for 
configuration files.
It is one of the most popular configuration libraries because it is reliable, and powerful. 
However, to use it you must write some boilerplate code to load the configuration. 
Even if you use the `ConfigBeanFactory.create` to load the configuration into a POJO, 
you still need to create and maintain the config POJO classes. 
It also does not work directly with records, and you are left to implement the whole java code boilerplate yourself.  

The tscfg library provides a way to generate a typesafe config file and associated Java classes from a template
spec file. 
This solves the boilerplate issue, using a single file as a source of truth.
Still to use it a manual flow or some custom scripts are needed to generate the files and classes.
For more details, see the [tscfg readme](https://github.com/carueda/tscfg).

This Gradle plugin wraps the tscfg library to provide a simple way to automatically generate the config files 
and configuration POJO classes.
It runs as part of the build process, so the generated files are always up to date.

For the moment, only Java config classes are supported.


## Usage

Basic configuration:

Kotlin DSL:

```kts
plugins {
  id("java")
  id("io.github.aleris.plugins.tscfg")
}

tscfg {
  packageName = "com.example"
  generateRecords = true
  addGeneratedAnnotation = true

  files {
    register("application") {
      specFile = file("src/tscfg/application.spec.conf")
      configFile = file("src/main/resources/application.conf")
    }
  }
}
```

Groovy DSL:

```groovy
plugins {
  id 'java'
  id 'io.github.aleris.plugins.tscfg'
}

tscfg {
  packageName = 'com.example'
  generateRecords = true
  addGeneratedAnnotation = true

  files {
    'application' {
      specFile = file('src/tscfg/application.spec.conf')
      configFile = file('src/main/resources/application.conf')
      className = 'ApplicationConfig'
    }
  }
}
```

The java plugin is required to use the plugin.

The `src/tscfg/application.spec.conf` spec/template file should contain a 
[tscfg schema definition](https://github.com/carueda/tscfg/wiki/template-generation), for example:

```hocon
# Configuration for API calls
api {
  # The URI of the API
  #@envvar API_URI
  uri: "string | http://localhost:8080",
}
```

This will generate a `src/main/resources/application.conf` file and a `com.example.ApplicationConfig` class
in `generated/sources` source set that can be used to load the config.

```java
Config config = ConfigFactory.load().resolve();
ApplicationConfig applicationConfig = new ApplicationConfig(config);
```

## Configuration

The plugin adds a `tscfg` extension to the project, which can be used to configure the plugin.

```kts
tscfg {
  // The package name of the generated config classes.
  // Defaults to a name derived from the Gradle project `group` property.
  // Applies to all generated classes.
  packageName = 'com.package.name'

  // Whether to generate the configuration file from the specification file.
  // If set to `false`, only the config class will be generated.
  // Defaults to `true`.
  generateConfigFile = true

  // The indentation used in the generated configuration file.
  // Defaults to two spaces.
  configFileIndent = '  '
  
  // Whether to include the generated configuration file in the generated resources set.
  // Defaults to `true`.
  // 
  // If set to `true`, the configuration file will be generated in project path
  // `build/generated/sources/io/github/aleris/tscfg/java/<package>/<className>.java`.
  // The tscfg directory in generated sources is included in the main java resources set.
  // 
  // If set to `false`, the configuration file will be generated in project path
  // `src/main/java/<package>/<className>.java`.
  outputInGeneratedResourceSet = true

  // Whether to include the `jakarta.annotation.Generated` annotation in the generated config class.
  // Defaults to `false`.
  // 
  // If set to `true`, the annotation will be included in the generated config class.
  // The jakarta dependency is required to use this annotation, and you must include it in your build.
  addGeneratedAnnotation = false
  
  // Whether to generate getters for the config class.
  // Defaults to `false`.
  // Mutually exclusive with `generateRecords`.
  generateGetters = false
  
  // Whether to generate records for the config class.
  // Defaults to `false`.
  // Mutually exclusive with `generateGetters`.
  generateRecords = false
  
  // Whether to use `java.util.Optional` for optional fields in the config class.
  // Defaults to `false`.
  useOptionals = false
  
  // Whether to use `java.time.Duration` for duration fields in the config class.
  // Defaults to `false`.
  useDurations = false
  
  // The configuration files to be processed by the tscfg plugin.
  // Multiple files can be added inside the `tscfg.files` block.
  // If not specified, the default configuration file "src/tscfg/application.spec.conf" will be used.
  files {
    // Name of the configuration file, used as a label and to derive some defaults if not specified.
    // Must be unique.
    'application' {
      // The specification or template file used to generate both the configuration file and the config class.
      // The default value is `src/tscfg/<name>.spec.conf` if not specified.
      specFile = file('src/tscfg/application.spec.conf')
      
      // The configuration file that will be generated from the specification file.
      // The default value is `src/main/resources/<name>.conf` if not specified.
      configFile = file('src/main/resources/application.conf')
      
      // The package name of the generated config class.
      // Can be used to override the global package name in the upper tscfg block of the configuration.
      packageName = 'com.package.name'
      
      // The class name of the generated config class.
      // The default value is derived from the configuration file path.
      // Default is `ApplicationConfig` if not specified.
      className = 'ApplicationConfig'
    }
  }
}
```
