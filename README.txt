Running Parallel Tests With Clojure
===================================
Clojure is a dialect of Lisp that runs on the JVM. It has very elegant syntax
and a novel approach to concurrency.

In this installment, we'll build a Clojure framework for running Selenium tests
in parallel. We'll use Clojure's Java interop to interact with Selenium, and
agents to distribute the work. Once you have the tests executing in parallel,
you can run them against your internal selenium farm or use our Sauce OnDemand
cloud hosted service to get running with no further configuration.

First, the framework, in about 50 lines of code:
    [selenium.clj]

Now let's define some tests and run them:
    [test-sauce.clj]
    
To run the system locally, you'll need the Selenium server and client jars and
the Clojure jar (we're using 1.1.0). You can use Sauce RC to get a Selenium
server up and running using a GUI-based installer. Then, just issue the
following command:

    java -jar selenium-server.jar&
    java -cp selenium-java-client-driver.jar:clojure.jar \
        clojure.main test-sauce.clj
(See the run-tests script, you can control the number of concurrent tests by
providing :num-agents N to run-tests)

To run it against our cloud hosted service without having to configure any
servers, simply follow the instructions at Sauce OnDemand documentation and
supply the right options to run-tests. It will probably look something like:

    (run-tests tests reporter
               :host "saucelabs.com"
               :command "{
                  \"username\" : \"SAUCE-USER-NAME\",
                  \"access-key\" : \"SAUCE-API-KEY\",
                  \"os\" : \"Windows 2003\",
                  \"browser\" : \"firefox\",
                  \"browser-version\" : \"\"}"
               :url "http://saucelabs.com")

You won't need to run any servers locally, just execute the clojure test:

    java -cp selenium-java-client-driver.jar:clojure.jar \
        clojure.main test-sauce.clj

Future enhancements to this framework might include better reporting, and
integration with the clojure.test testing framework.

All of the code from this installment, including the needed jar files can be
found at http://github.com/saucelabs/parallel-clojure-example.
