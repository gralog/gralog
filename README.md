Gralog
======

[![Build Status](https://travis-ci.org/gralog/gralog.svg?branch=master)](https://travis-ci.org/gralog/gralog)

Interactively explore graphs, algorithms, logics and games.

Run
-----

A compiled version is in build/dist. Just launch `gralog-fx.jar`. Note that the other files should remain in the same directory. On Windows and macOS it should be possible by (double) clicking on gralog-fx.jar. On Linux, if clicking does not work, try 

`java -jar <path/to/>gralog-fx.jar`.

If you want to use Gralog with Python, you should install the necessary Python libraries first. Please, read doc/manual/gralog.pdf for details.


Build
-----

For a detailed explanation, read doc/manual/gralog.pdf.

This is a gradle project.  You can build and run Gralog by following these steps:

1. Install the Java Development Kit (JDK) 11 or later.

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
