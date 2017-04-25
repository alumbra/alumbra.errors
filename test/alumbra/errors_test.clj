(ns alumbra.errors-test
  (:require [clojure.test :refer :all]
            [clojure.test.check
             [properties :as prop]
             [clojure-test :refer [defspec]]]
            [clojure.string :as string]
            [clojure.spec :as s]
            [alumbra
             [errors :as errors]
             spec]))

;; ## Parser Error

(defspec t-explain-data-parser-error 1000
  (prop/for-all
    [error (s/gen :alumbra/parser-error)]
    (let [value {:alumbra/parser-errors [error]}
          [explained-error] (is (errors/explain-data value ""))]
      (and (or (empty? (:alumbra/locations error))
               (string? (:context explained-error)))
           (string? (:message explained-error))
           (sequential? (:locations explained-error))
           (contains? explained-error :hint)))))

(defspec t-explain-data-validation-error 1000
  (prop/for-all
    [error (s/gen :alumbra/validation-error)]
    (let [value {:alumbra/validation-errors [error]}
          [explained-error] (is (errors/explain-data value ""))]
      (and (or (empty? (:alumbra/locations error))
               (string? (:context explained-error)))
           (string? (:message explained-error))
           (sequential? (:locations explained-error))
           (contains? explained-error :hint)))))

(defspec t-explain-data-multiple-validation-errors 200
  (prop/for-all
    [errors (s/gen :alumbra/validation-errors)]
    (let [value {:alumbra/validation-errors errors}]
      (is (= (count errors)
             (count (errors/explain-data value "")))))))

(defspec t-explain-data-multiple-parser-errors 200
  (prop/for-all
    [errors (s/gen :alumbra/parser-errors)]
    (let [value {:alumbra/parser-errors errors}]
      (is (= (count errors)
             (count (errors/explain-data value "")))))))
