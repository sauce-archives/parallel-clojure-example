Running Parallel Tests With Clojure
===================================


Clojure (http://clojure.org/) is a dialect of LISP the runs on the JVM.
It has very elegant syntax and novel approach to concurrency.

In this installed, we'll build a framework for running tests in parallel.
We will utilize Clojure's Java interop and concurrency features to do this in
less than 50 lines of code.

Our framework is listed in [selenium.clj].

View a way to use the framework in [test-one.clj].

To run the system, you'll need the Selenium server and client jars and the
Clojure jar (we're using 1.1.0). The issue the following command:

    java -cp selenium-java-client-driver.jar:selenium-server.jar:clojure.jar \
        clojure.main test-sauce.clj

You will see three tests running in the same time (the maximal number of
concurrent tests is define in selenium.clj as *num-agents*).

Future enhancements to this framework can be better reporting, integration with
Clojure's test/is testing framework and more.
