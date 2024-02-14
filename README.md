[![](https://jitpack.io/v/hmcts/cmc-pdf-service-client.svg)](https://jitpack.io/#hmcts/cmc-pdf-service-client)
[![JitPack Badge](https://github.com/hmcts/cmc-pdf-service-client/actions/workflows/gradle.yml/badge.svg)](https://github.com/hmcts/cmc-pdf-service-client/actions/workflows/gradle.yml)

# PDF service client

This is a client library for pdf-service, which allows to generate read-only PDFs based on given template in HTML/Twig format and
placeholder data in JSON format.

## Getting started

### Prerequisites

- [JDK 17](https://www.oracle.com/java)

### Building

The project uses [Gradle](https://gradle.org) as a build tool but you don't have to install it locally since there is a
`./gradlew` wrapper script.

To build project please execute the following command:

```bash
    ./gradlew build
```

## Developing

### Unit tests

To run all unit tests please execute following command:

```bash
    ./gradlew test
```

### Coding style tests

To run all checks (including unit tests) please execute following command:

```bash
  ./gradlew check
```

## Versioning

We use [SemVer](http://semver.org/) for versioning.
For the versions available, see the tags on this repository.

To release a new version add a tag with the version number and push this up to the origin repository. This will then
build and publish the release to maven.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
