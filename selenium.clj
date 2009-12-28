; Copyright 2009 Sauce Labs Inc
; 
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
; 
;    http://www.apache.org/licenses/LICENSE-2.0
; 
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(import '[org.openqa.selenium.server SeleniumServer]
        '[com.thoughtworks.selenium DefaultSelenium])

(defn- run-with-client
  "Run a test function with new client"
  [test-fn]
  (let [client (new DefaultSelenium "localhost" 
                    4444 "*firefox" "http://localhost/")]
    (.start client)
    (try
      (test-fn client)
      (catch Exception e false)
      (finally
        (.stop client)))))

(defn- run-single-test
  "Run a single test, append test result to results"
  [test results]
  (let [test-fn (test :test)
        value (run-with-client test-fn)
        res {:test (test :name) :value value}]
    (dosync (alter results (fn [old] (conj old res))))))

(defn- gen-agents
  [num-agents]
  (take num-agents (map agent (repeat nil)))) 

(def *num-agents* 4) ; Number of agents to use

(defn run-tests
  "Run tests in parallel, return list of test results.
  After tests are done, passes the list of results to reporter.

  A test has the format
  {
    :name <test-name>
    :test (fn [client] ...)
  }
  "
  [tests reporter]
  (let [agents (gen-agents *num-agents*)
        results (ref [])
        server (new SeleniumServer)]
    (.start server)
    (doseq [[test agent] (map vector tests (cycle agents))]
      (send agent (fn [_] (run-single-test test results))))
    (doseq [agent agents] (await agent)) ; Wait for tests to finish
    (.stop server)
    (shutdown-agents)
    (reporter @results)
    @results))

(defn tests-failed?
  "Check if all tests passed"
  [results]
  (not (empty? (filter false? (map :value results)))))
