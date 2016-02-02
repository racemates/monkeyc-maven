# monkeyc-maven
Maven plugin for Garmin Connect IQ projects.

## Configuration
Create an environment variable GARMIN_HOME which points to your Connect IQ SDK installation. The plugin uses the same project layout as in Eclipse.
```
manifest.xml
source/
resources/
pom.xml
```

Then simply add the plugin to your pom file.
```xml
<plugins>
    <plugin>
        <groupId>se.racemates.maven</groupId>
        <artifactId>monkeyc-maven</artifactId>
        <version>1.1</version>
    </plugin>
</plugins>
```

## Usage
To compile run
```bash
mvn monkeyc:compile
```
And to run tests
```bash
mvn monkeyc:test
```


