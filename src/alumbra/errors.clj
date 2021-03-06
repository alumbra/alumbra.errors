(ns alumbra.errors
  (:require [alumbra.errors
             [context :refer [context-for]]
             [templates :as templates]]
            [clojure.string :as string]))

;; ## Parser Errors

(defn- parser-error-message
  [{:keys [alumbra/location alumbra/error-message]}]
  (format "Syntax Error (%d:%d): %s"
          (inc (:row location))
          (inc (:column location))
          error-message))

(defn- explain-parser-error
  [{:keys [alumbra/location] :as error} input-string]
  (merge
    (templates/render-for
      :parser-error
      (assoc error :raw-message (parser-error-message error)))
    {:locations [location]
     :context   (context-for [location] input-string)}))

;; ## Validation Error

(defn- explain-validation-error
  [{:keys [alumbra/locations
           alumbra/validation-error-class]
    :as error}
   input-string]
  (merge
    (templates/render-for :validation-error error)
    {:locations locations
     :context   (context-for locations input-string) }))

;; ## Format

(defn ^{:added "0.1.0"} format-data
  "Format the result of [[explain-data]] as a string."
  [explained-errors]
  (when (seq explained-errors)
    (->> (for [{:keys [message hint context]} explained-errors]
           (->> (vector (str "== " message) context hint)
                (filter identity)
                (string/join "\n\n")))
         (string/join "\n\n"))))

;; ## Explain

(defn ^{:added "0.1.0"} explain-data
  "Given the result of a parser/validation operation, as well as the original
   input as a string, generate a seq of maps describing any occured errors:

   - `:locations`: the error locations,
   - `:context`:   the part of the `input-string` affected,
   - `:message`:   an error message,
   - `:hint`:      an error hint for resolution, describing common causes.
   "
  [{:keys [alumbra/parser-errors
           alumbra/validation-errors]}
   input-string]
  (seq
    (cond parser-errors
          (map #(explain-parser-error % input-string) parser-errors)

          validation-errors
          (map #(explain-validation-error % input-string) validation-errors))))

(defn ^{:added "0.1.0"} explain-str
  "Given the result of a parser/validation operation, as well as the original
   input as a string, generate a description of any occured errors, including:

   - the original error message, including location(s),
   - the part of the code affected, as well as a few lines of context,
   - a hint on common causes for the given error.
   "
  [result input-string]
  (some-> (explain-data result input-string)
          (format-data)))

(defn ^{:added "0.1.0"} explain
  "Like [[explain-str]] but prints to stdout."
  [result input-string]
  (some->> (explain-str result input-string)
           (println)))
