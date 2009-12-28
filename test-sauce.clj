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

(require 'selenium)

(def test-google
  {
   :name "google"
   :test (fn [client]
           (doto client
             (.open "http://www.google.com")
             (.type "q" "Sauce Labs")
             (.click "btnG")
             (.waitForPageToLoad "5000"))
           (.isTextPresent client "Selenium"))
   })

(def test-yahoo
  {
   :name "sauce"
   :test (fn [client]
           (doto client
             (.open "http://yahoo.com")
             (.type "p" "Sauce Labs")
             (.click "search-submit")
             (.waitForPageToLoad "5000"))
           (.isTextPresent client "Selenium"))
   })


(def test-bing
  {
   :name "bing"
   :test (fn [client]
           (doto client
             (.open "http://www.bing.com")
             (.type "q" "Sauce Labs")
             (.click "go")
             (.waitForPageToLoad "5000"))
           (.isTextPresent client "Selenium"))
   })

(def tests [test-google test-yahoo test-bing])

; Our fancy reporter :)
(defn reporter
  [results]
  (doseq [r results]
    (println r)))

(let [results (run-tests tests reporter)]
  (if (tests-failed? results)
    (System/exit 1)))
