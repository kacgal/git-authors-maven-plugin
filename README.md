[![](https://jitpack.io/v/kacgal/git-authors-maven-plugin.svg)](https://jitpack.io/#kacgal/git-authors-maven-plugin)

##### Usage

```xml
<project>
  <build>
    <plugins>
      <plugin>
        <groupId>com.github.kacgal</groupId>
        <artifactId>git-authors-maven-plugin</artifactId>
        <version>1.0</version>
        <executions>
          <execution>
            <id>get-git-authors</id>
            <goals>
              <goal>git-authors</goal>
            </goals>
            </execution>
          </executions>
          <configuration>
            <format>"$${name}" &lt;$${email}></format>
            <joiner>,</joiner>
            <sort>FIRST_COMMIT</sort>
            <reverse>false</reverse>
          </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

##### Configuration
###### format
The format of the printed author name/email, ${name} and ${email} respectively
###### joiner
String to join the authors with
###### sort
Sort the authors, can be either of: FIRST_COMMIT, NUM_COMMITS
###### reverse
Sort in reverse