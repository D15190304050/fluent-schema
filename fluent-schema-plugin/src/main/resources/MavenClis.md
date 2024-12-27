# Maven command template
```shell
mvn <groupId>:<artifactId>:<version>:<goal> -D<property>=<value>
```


# Command to generate forward sql and write into file.
```shell
mvn stark.coderaider:fluent-schema-plugin:1.0-SNAPSHOT:generate-forward-sql -DsqlOutputFilePath=D:/DinoStark/Temp/k.sql -DschemaPackage="stark.coderaider.tester.schemas"
```

# POM configuration of plugin
```xml
<plugin>
    <groupId>stark.coderaider</groupId>
    <artifactId>fluent-schema-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <entityPackage>stark.coderaider.test.entities</entityPackage>
        <schemaPackage>stark.coderaider.test.entities.schema</schemaPackage>
    </configuration>
</plugin>
```