VERSION=1.0.0

default: versioncheck

build-all: clean stage

stop:
	./gradlew --stop

clean:
	./gradlew clean

compile: build

build:
	./gradlew build -xtest

tests:
	./gradlew --rerun-tasks check

jar:
	./gradlew uberJar

dist:
	./gradlew installDist

stage:
	./gradlew stage

versioncheck:
	./gradlew dependencyUpdates

kdocs:
	./gradlew dokkaHtml dokkaGfm

publish:
	./gradlew publishToMavenLocal

upgrade-wrapper:
	./gradlew wrapper --gradle-version=8.9 --distribution-type=bin
