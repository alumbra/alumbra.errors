(ns alumbra.errors.templates
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [selmer.util :refer [without-escaping]]
            [selmer.parser :as template]
            [selmer.filters :as filters]))

;; ## Formatters

(defn- format-type-description
  [{:keys [type-description type-name non-null?]}]
  (cond-> (if type-description
            (format "[%s]" (format-type-description type-description))
            type-name)
    non-null? (str "!")))

(defn- format-value
  [{:keys [alumbra/value-type] :as value}]
  (case value-type
    :string  (pr-str (:alumbra/string value))
    :integer (:alumbra/integer value)
    :float   (:alumbra/float value)
    :boolean (:alumbra/boolean value)
    :enum    (:alumbra/enum value)
    :list    (->> (:alumbra/list value)
                  (map format-value)
                  (string/join ", ")
                  (format "[%s]"))
    :object  (->> (:alumbra/object value)
                  (map
                    (fn [{:keys [alumbra/field-name alumbra/value]}]
                      (str field-name ":" (format-value value))))
                  (string/join ", ")
                  (format "{%s}"))
    :variable (str "$" (:alumbra/variable-name value))
    :null    "null"
    (pr-str value)))

(filters/add-filter! :alumbra/graphql-type  #'format-type-description)
(filters/add-filter! :alumbra/graphql-value #'format-value)

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
  {:post [(or (nil? %)
              (not (string/blank? (:message %))))]}
  (when (io/resource template-path)
    (let [result (without-escaping
                   (template/render-file
                     template-path
                     error
                     {:custom-resource-path nil}))
          [message hint] (string/split result #"\n----\n" 2)]
      {:message (-> message
                    (string/replace #"\n+" " ")
                    (string/trim))
       :hint    (if  (some-> hint string/blank? not)
                  (string/trim hint))})))

(defn- default-path-for
  [error-class]
  (case error-class
    :validation-error "validation-errors/default.txt"
    :parser-error "parser-errors/default.txt"))

(defn render-for
  [error-class error]
  (->> [(path-for error-class error)
        (default-path-for error-class)]
       (filter identity)
       (map #(str "alumbra/errors/" %))
       (some #(render % error))))
