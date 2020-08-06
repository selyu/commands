# commands
Fork of [JonahSeguin](https://github.com/jonahseguin) 's command library [drink](https://github.com/jonahseguin/drink)

## What is this?
From the original project's README:
```
drink is a command library designed to remove the repetitive code involved with writing commands for Spigot plugins specifically.

drink takes an IoC (Inversion of Control) based approach using a very simple Dependency-Injection design pattern inspired by 
Google's Guice library and sk89q's Intake command library.
```

## Installing
1. Clone this repository: `git clone git@github.com:selyu/commands.git`
2. Enter the directory: `cd commands`
3. Build & install with Maven: `mvn clean package install`

Add this to your `pom.xml` `<dependencies>`:
```xml
<dependency>
  <groupId>org.selyu.commands</groupId>
  <artifactId><!-- Module--></artifactId>
  <version><!-- Version --></version>
  <scope>compile</scope>
</dependency>
```