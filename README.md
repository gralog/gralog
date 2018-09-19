Gralog
======

[![Build Status](https://travis-ci.org/gralog/gralog.svg?branch=master)](https://travis-ci.org/gralog/gralog)

Interactively explore graphs, algorithms, logics and games.

Build
-----

This is a gradle project.  You can build and run Gralog by following these steps:

1. Install the Java Development Kit (JDK) 10 or newer.

2. Run `./gradlew`.

  Note: On Windows you probably need to call `gradlew.bat` instead.

Tests
-----

To run the test suite, call:

    ./gradlew test

Contributing
------------

We use github for issue tracking and pull requests.

Continuous integration on github runs a style checker that checks the
indentation, the capitalization of variable names and similar aspects.
If you would like to submit a pull request, it would be greatly
appreciated if your code passes the style checker.

You can run this check yourself by calling:

    ./gradlew check
