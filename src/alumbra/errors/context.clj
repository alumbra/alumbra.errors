(ns alumbra.errors.context
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn- find-line
  [input-string row]
  (with-open [in (io/reader (.getBytes input-string "UTF-8"))]
    (->> (line-seq in)
           (map-indexed #(vector (inc %1) %2))
           (drop row)
           (first))))

(defn- format-line
  [[line-number line]]
  (format "%-3s %s" (str line-number "|")  line))

(defn- format-caret
  [column]
  (str "    " (apply str (repeat column " ")) "^"))

(defn error-with-context
  [{:keys [row column]} input-string error-message & [error-hint]]
  (let [line (find-line input-string row)]
    (->> (concat
           [error-message]
           (when (or line error-hint)
             [""])
           (when line
             (vector
               (format-line line)
               (format-caret column)))
           [error-hint])
         (filter identity)
         (string/join "\n"))))
