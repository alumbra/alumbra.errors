(defproject alumbra/errors "0.1.0-SNAPSHOT"
  :description "Human-readable Errors for Alumbra."
  :url "https://github.com/alumbra/alumbra.ring"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2017
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14" :scope "provided"]
                 [alumbra/spec "0.1.6" :scope "provided"]]
  :profiles {:dev
             {:dependencies
              [[alumbra/validator "0.1.0"]
               [alumbra/parser "0.1.5"]
               [alumbra/analyzer "0.1.8"]]}
             :codox
             {:plugins [[lein-codox "0.10.3"]]
              :dependencies [[codox-theme-rdash "0.1.1"]]
              :codox {:project {:name "alumbra.errors"}
                      :metadata {:doc/format :markdown}
                      :themes [:rdash]
                      :source-uri "https://github.com/alumbra/alumbra.errors/blob/v{version}/{filepath}#L{line}"
                      :namespaces [alumbra.errors]}}}
  :aliases {"codox" ["with-profile" "+codox" "codox"]}
  :pedantic? :abort)
