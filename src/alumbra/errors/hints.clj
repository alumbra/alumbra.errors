(ns alumbra.errors.hints
  (:require [clojure.java.io :as io]))

(defn- hint-for*
  [error-key]
  (let [resource-path (str"alumbra/hints/"
                        (name error-key)
                        ".txt")]
    (some->> resource-path
             (io/resource)
             (slurp))))

(let [memo-hint (memoize hint-for*)]
  (defn hint-for
    [error-key]
    (memo-hint error-key)))
