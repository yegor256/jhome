[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/jhome)](http://www.rultor.com/p/yegor256/jhome)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/jhome/actions/workflows/mvn.yml/badge.svg)](https://github.com/yegor256/jhome/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/jhome)](http://www.0pdd.com/p?name=yegor256/jhome)
[![Maven Central](https://img.shields.io/maven-central/v/com.yegor256/jhome.svg)](https://maven-badges.herokuapp.com/maven-central/com.yegor256/jhome)
[![Javadoc](http://www.javadoc.io/badge/com.yegor256/jhome.svg)](http://www.javadoc.io/doc/com.yegor256/jhome)
[![codecov](https://codecov.io/gh/yegor256/jhome/branch/master/graph/badge.svg)](https://codecov.io/gh/yegor256/jhome)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/jhome)](https://hitsofcode.com/view/github/yegor256/jhome)
![Lines of code](https://img.shields.io/tokei/lines/github/yegor256/jhome)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/jhome/blob/master/LICENSE.txt)

In your Java application you may want to run another Java application.
In order to do this, you need to know where is the `bin/java` executable.
It's in the `JAVA_HOME` (environment variable), which is not always set.
Instead of looking for this place manually, just use this small one-class library.

First, you add this to your `pom.xml`:

```xml
<dependency>
  <groupId>com.yegor256</groupId>
  <artifactId>jhome</artifactId>
  <version>0.0.2</version>
</dependency>
```

Then, you use it like this:

```java
import com.yegor256.jhome;
Path p = new Jhome().path("bin/java");
```

The result is the location of `bin/java` inside `JAVA_HOME`.

## How to Contribute

Fork repository, make changes, send us a [pull request](https://www.yegor256.com/2014/04/15/github-guidelines.html).
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
$ mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 8+.
