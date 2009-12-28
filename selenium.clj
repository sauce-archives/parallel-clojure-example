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

(import '[com.thoughtworks.selenium DefaultSelenium])

(defn- run-with-client
  "Run a test function with new client"
  [test-fn opts]
  (let [client (new DefaultSelenium (:host opts) (:port opts) (:command opts) 
                    (:url opts))]
    (.start client)
    (try
      (test-fn client)
      (catch Exception e false)
      (finally
        (.stop client)))))

(defn- run-single-test
  "Run a single test, append test result to results"
  [test results opts]
  (let [test-fn (test :test)
        value (run-with-client test-fn opts)
        res {:test (test :name) :value value}]
    (dosync (alter results (fn [old] (conj old res))))))

(defn- gen-agents
  [num-agents]
  (take num-agents (map agent (repeat nil)))) 

(defn- parse-options
  "Get options as hash map, provide defaults"
  [args]
  (let [opts (apply hash-map args)] ; Convert [:port 4444] to {:port 4444}
    (-> opts
      (assoc :port (:port opts 4444))
      (assoc :host (:host opts "localhost"))
      (assoc :command (:command opts "*firefox"))
      (assoc :url (:url opts "http://localhost/"))
      (assoc :num-agents (:num-agents opts 4)))))

(defn run-tests
  "Run tests in parallel, return list of test results.
  After tests are done, passes the list of results to reporter.

  A test has the format
  {
    :name <test-name>
    :test (fn [client] ...)
  }
  "
  [tests reporter & args]
  (let [opts (parse-options args)
        agents (gen-agents (:num-agents opts))
        results (ref [])]
    (doseq [[test agent] (map vector tests (cycle agents))]
      (send agent (fn [_] (run-single-test test results opts))))
    (doseq [agent agents] (await agent)) ; Wait for tests to finish
    (shutdown-agents)
    (reporter @results)
    @results))

(defn tests-failed?
  "Check if all tests passed"
  [results]
  (not (empty? (filter false? (map :value results)))))
