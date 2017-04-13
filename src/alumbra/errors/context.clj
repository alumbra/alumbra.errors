(ns alumbra.errors.context
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn- find-line
  [input-string row]
  (with-open [in (io/reader (.getBytes input-string "UTF-8"))]
    (let [[before [line]]  (->> (line-seq in)
                                (map-indexed #(vector (inc %1) %2))
                                (split-at row))]
      {:before (take-last 2 before)
       :line   line})))

(defn- format-line
  [[line-number line]]
  (format "%-3s %s" (str line-number "|")  line))

(defn- format-caret
  [column]
  (str "    " (apply str (repeat column " ")) "^"))

(defn context-for
  [{:keys [row column]} input-string]
  (let [{:keys [before line]} (find-line input-string row)]
    (when (or before line)
      (->> (concat
             (map format-line before)
             (when line
               (vector
                 (format-line line)
                 (format-caret column))))
           (string/join "\n")))))
