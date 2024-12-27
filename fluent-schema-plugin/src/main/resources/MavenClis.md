# Maven command template
<code>
mvn &lt;groupId>:&lt;artifactId>:&lt;version>:&lt;goal> -D&lt;property>=&lt;value>
</code>


# Command to generate forward sql and write into file.
<code>mvn stark.coderaider:fluent-schema-plugin:1.0-SNAPSHOT:generate-forward-sql -DsqlOutputFilePath=D:/DinoStark/Temp/k.sql -DschemaPackage="stark.coderaider.tester.schemas"</code>