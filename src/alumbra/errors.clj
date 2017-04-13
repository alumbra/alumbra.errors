(ns alumbra.errors
  (:require [alumbra.errors
             [context :refer [context-for]]
             [hints :refer [hint-for]]]
            [clojure.string :as string]))

;; ## Parser Errors

(defn- parser-error-message
  [{:keys [alumbra/location alumbra/error-message]}]
  (format "Syntax Error (%d:%d): %s"
          (inc (:row location))
          (inc (:column location))
          error-message))

(defn- format-parser-error
  "Format a single parser error, including a snippet describing the location of
   the error."
  [{:keys [alumbra/location] :as error} input-string]
  (->> (vector
         (parser-error-message error)
         (context-for location input-string)
         (hint-for :parser-error error))
       (filter identity)
       (string/join "\n\n")))

(defn- format-parser-errors
  "Format a parser error map, including a snippet describing the location of
   the error for each failure."
  [parser-errors input-string]
  (->> parser-errors
       (map #(format-parser-error % input-string))
       (string/join "\n\n-----\n\n")))

;; ## Explain

(defn ^{:added "0.1.0"} explain-str
  "Given the result of a parser/validation operation, as well as the original
   input as a string, generate a description of any occured errors, including:

   - the original error message, including location(s),
   - the part of the code affected, as well as a few lines of context,
   - a hint on common causes for the given error."
  [{:keys [alumbra/parser-errors
           alumbra/validation-errors]}
   input-string]
  (cond parser-errors
        (format-parser-errors parser-errors input-string)

        validation-errors
        nil))

(defn ^{:added "0.1.0"} explain
  "Like [[explain-str]] but prints to stdout."
  [result input-string]
  (some->> (explain-str result input-string)
           (println)))
