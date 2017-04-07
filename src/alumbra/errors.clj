(ns alumbra.errors
  (:require [alumbra.errors
             [context :refer [error-with-context]]
             [hints :refer [hint-for]]]
            [clojure.string :as string]))

;; ## Parser Errors

(defn format-parser-error
  "Format a single parser error, including a snippet describing the location of
   the error."
  [{:keys [alumbra/location alumbra/error-message] :as x} input-string]
  (error-with-context
    location
    input-string
    (format "Syntax Error (%d:%d): %s"
            (inc (:row location))
            (inc (:column location))
            error-message)))

(defn format-parser-errors
  "Format a parser error map, including a snippet describing the location of
   the error for each failure."
  [{:keys [alumbra/parser-errors]} input-string]
  (let [errors (->> parser-errors
                    (map #(format-parser-error % input-string))
                    (string/join "\n"))]
    (str errors "\n\n" (hint-for :parser-error))))
