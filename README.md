# Shadowhunt properties Validator

This Maven plugin ensures that all translation properties are present in all
languages and also that the properties are in the correct encoding.

*Usage*

```xml
...
<plugin>
    <groupId>de.shadowhunt.maven.plugins</groupId>
    <artifactId>properties-maven-plugin</artifactId>
    <version>1.0.1</version>
    <executions>
        <execution>
            <phase>verify</phase>
            <goals>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <encoding>ASCII</encoding>
        <master>src/main/resources/i18n/translation.properties</master>
        <messagePropertiesPatterns>
            <messagePropertiesPattern>src/main/resources/i18n/*.properties</messagePropertiesPattern>
        </messagePropertiesPatterns>
    </configuration>
</plugin>
...
```
