Building RESTPerf
========================

Install Prerequisites
---------------------

* JDK 7


git checkout
------------
### clone

`git clone https://github.com/mastalx/RESTPerf.git`


Gradle (gradlew) Tasks
---------------------

Important use gradlew.bat instead of gradle.bat.

### Eclipse Setup Task

`gradlew cleaneclipse eclipse  --refresh-dependencies`

### Build Jar (Deployment)

`radlew jar --refresh-dependencies`


### TEST

`gradlew test --refresh-dependencies`

