(ns alumbra.errors.hints
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [selmer.parser :as template]))

;; ## Parser Errors

(def ^:private parser-error-rules
  [[#"no viable alternative at input '<EOF>'"
    :unexpected-eof]
   [#"token recognition error at: '\".*'"
    :unbalanced-string]
   [#"mismatched input '}'.*"
    :early-closing-brace]
   [#"extraneous input '}'.*"
    :unbalanced-closing-brace]])

(defn- path-for-parser-error
  [{:keys [alumbra/error-message]}]
  (first
    (for [[regex error-key] parser-error-rules
          :when (re-matches regex error-message)]
      (str "parser-errors/" (name error-key) ".txt"))))

;; ## Validation Errors

(defn- path-for-validation-error
  [{:keys [alumbra/validation-error-class]}]
  (str "validation-errors/"
       (namespace validation-error-class)
       "/"
       (name validation-error-class) ".txt"))

;; ## Hints

(defn- path-for
  [error-class error]
  (case error-class
    :parser-error (path-for-parser-error error)
    :validation-error (path-for-validation-error error)))

(defn hint-for
  [error-class error]
  (let [path (path-for error-class error)
        full-path (str "alumbra/hints/" path)]
    (string/trim
      (template/render-file full-path error))))
