(defproject alumbra/errors "0.1.0-SNAPSHOT"
  :description "Human-readable Errors for Alumbra."
  :url "https://github.com/alumbra/alumbra.ring"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2017
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14" :scope "provided"]
                 [alumbra/spec "0.1.6" :scope "provided"]
                 [selmer "1.10.7"]]
  :profiles {:dev
             {:dependencies
              [[org.clojure/test.check "0.9.0"]]}
             :codox
             {:plugins [[lein-codox "0.10.3"]]
              :dependencies [[codox-theme-rdash "0.1.2"]]
              :codox {:project {:name "alumbra.errors"}
                      :metadata {:doc/format :markdown}
                      :themes [:rdash]
                      :source-uri "https://github.com/alumbra/alumbra.errors/blob/v{version}/{filepath}#L{line}"
                      :namespaces [alumbra.errors]}}}
  :aliases {"codox" ["with-profile" "+codox" "codox"]}
  :pedantic? :abort)
