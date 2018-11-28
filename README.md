[![Travis badge](https://api.travis-ci.org/hmcts/cmc-pdf-service-client.svg?branch=master)](https://travis-ci.org/hmcts/cmc-pdf-service-client)
[![Download](https://api.bintray.com/packages/hmcts/hmcts-maven/pdf-service-client/images/download.svg) ](https://bintray.com/hmcts/hmcts-maven/pdf-service-client/_latestVersion)

# PDF service client

This is a client library for pdf-service, which allows to generate read-only PDFs based on given template in HTML/Twig format and 
placeholder data in JSON format. 

## Getting started

### Prerequisites

- [JDK 8](https://www.oracle.com/java)

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

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
