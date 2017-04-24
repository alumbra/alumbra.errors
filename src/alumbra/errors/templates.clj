(ns alumbra.errors.templates
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
    :parser-error     (path-for-parser-error error)
    :validation-error (path-for-validation-error error)))

(defn- render
  [template-path error]
  (when (io/resource template-path)
    (let [result (-> template-path
                     (template/render-file error)
                     (string/trim))]
      (assert (not (empty? result)))
      result)))

(defn hint-for
  [error-class error]
  (when-let [path (path-for error-class error)]
    (-> (str "alumbra/errors/hints/" path)
        (render error))))

(defn- default-message-path-for
  [error-class]
  (case error-class
    :validation-error "validation-errors/default.txt"))

(defn message-for
  [error-class error]
  (->> [(path-for error-class error)
        (default-message-path-for error-class)]
       (map #(str "alumbra/errors/messages/" %))
       (some #(render % error))))
