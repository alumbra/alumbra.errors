(ns alumbra.errors.context
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

;; ## Calculate

(def ^:private context-rows-count 3)

(defn- context-prefix
  [row]
  (range
    (max (- row context-rows-count -1) 0)
    (inc row)))

(defn- calculate-rows-to-show
  [locations]
  (->> (map :row locations)
       (sort)
       (partition-all 2 1)
       (mapcat
         (fn [[a b]]
           (cond (not b)
                 (context-prefix a)

                 ;; if there would be <= one row between the two blobs,
                 ;; merge them.
                 (<= (- b a 1) context-rows-count)
                 (concat (context-prefix a) (range a (inc b)))

                 :else
                 (concat (context-prefix a) (context-prefix b)))))
       (into #{})))

;; ## Format

(defn- format-line
  [row line]
  (format "%-3s %s" (str (inc row) "|")  line))

(defn- format-carets
  [locations]
  (let [deltas (->> locations
                    (map :column)
                    (sort)
                    (cons -1)
                    (distinct)
                    (partition 2 1)
                    (keep
                      (fn [[a b]]
                        (if b
                          (- b a)))))]
    (reduce
      (fn [s delta]
        (str s (format (str "%" delta "s") "^")))
      "    "
      deltas)))

(defn context-for
  [locations input-string]
  (let [by-row (group-by :row locations)
        show?  (calculate-rows-to-show locations)]
    (with-open [in (io/reader (.getBytes input-string "UTF-8"))]
      (->> (line-seq in)
           (map vector (range))
           (mapcat
             (fn [[index line]]
               (vector
                 (if (show? index)
                   (format-line index line))
                 (if-let [caret-locations (by-row index)]
                   (format-carets caret-locations)))))
           (filter identity)
           (string/join "\n")))))
