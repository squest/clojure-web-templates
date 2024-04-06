(ns app.utils
  (:require
    [clj-uuid :as uuid]
    [clojure.edn :as edn]
    [clojure.string :as cs]
    [clojure.pprint :refer [pprint]]
    [java-time :as t]))

;; Set of useful utilities to deal with typical web devs

(defn now
  []
  (let [[date time] (-> (t/local-date-time)
                        (str)
                        (subs 0 19)
                        (cs/split #"T"))]
    (str date " " time)))

(defn freq-by
  "Like frequencies but with function"
  [f col]
  (->> (group-by f col)
       (map #(let [[k v] %] [k (count v)]))
       (into {})))

(defn num->per
  "Convert decimals into percentage string"
  [num]
  (str (subs (str (* 100 num)) 0 5) "%"))

(defn info [& body]
  (apply println "INFO :" body))

(defn error [& body]
  (apply println "ERROR :" body))

(def pres clojure.pprint/pprint)

(defn let-pres
  [exprs]
  (pres exprs)
  exprs)

(defn cslurp
  "Helper function to easily slurp and read-string a file"
  [fname]
  ((comp edn/read-string slurp) fname))

(defn cspit
  "Helper function to beautifully print clojure to file"
  [fname data]
  (->> data pprint with-out-str (spit fname)))

(defn cstr
  [data]
  (with-out-str (pprint data)))

(defn uuid
  "When given zero argument, it returns a uuid/v1, given one arguments, it returns
  a list of n uuids."
  ([]
   (cs/replace (str (uuid/v1)) #"-" ""))
  ([n]
   (repeatedly n uuid)))

;; (defn update-config-vals [map vals f]
;;   (reduce #(update-in % [%2] f) map vals))

(defn read-config
  "Reading the config, either intra-project or extra-project"
  []
  (cslurp "resources/config.edn"))

(defn update-config-vals-flat-format [map vals f]
  (reduce #(update-in % [%2] f) map vals))

(defn not-empty?
  [obj]
  (cond
    (string? obj) (->> (cs/trim obj) not-empty)
    (number? obj) (identity obj)
    (boolean? obj) (boolean obj)
    :else (not-empty obj)))

(defn empty-field-exist?
  [obj fields]
  (let [result (map #(not-empty? (% obj)) fields)]
    (not (empty? (filter nil? result)))))

(defn empty-fields
  [obj fields]
  (let [result (map #(not-empty? (% obj)) fields)]
    (->> (filter #(nil? (nth result %)) (range (count result)))
         (#(map (fn [x] (nth fields x)) %)))))

(defn indexOf [_vec _x]
  "Find the first index of obj in a list"
  (->> (map first
            (filter #(= (second %) _x)
                    (map-indexed vector _vec)))
       first))

(defn find-first
  [f coll]
  (first (filter f coll)))

