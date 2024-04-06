; Created: 2012-08-29 15:00:00
(ns mongo
  (:require
    [monger.core :as mg]
    [monger.collection :as mc]
    [app.utils :refer :all]
    [cheshire.core :as json]))

(def data {:tree      []
           :flattened []})

(defonce mongo-db (atom nil))

(defonce db (atom nil))

(defn mongo-config []
  (-> (read-config) :db :db-mongo))

(defn get-db
  []
  (reset! db (mg/get-db @mongo-db (:db (mongo-config)))))

(defn sg-for-collection
  "Prepare the skillgroup data for inserting into mongodb"
  []
  ;; just get it from flattened data
  ;; flattened data are already in clojure format
  ;; u don't need to parse it
  (let [sgs (get data :flattened)]
    (-> (fn [sg] (assoc sg :_id (:sg-id sg)))
        (mapv sgs))))

(defn insert-skillgroup
  "Insert the skillgroup data into mongodb, data is the new skillgroup data"
  [db data]
  (mc/insert-batch db "skillgroups" data))


(defn connect
  []
  (reset! mongo-db (mg/connect (mongo-config))))

(defn disconnect
  []
  (reset! mongo-db (mg/disconnect @mongo-db)))

(defn add-children
  [data]
  (letfn [(group-children [group]
            (let [children (->> data
                                (filter #(= (:parent-id %) (:sg-id group)))
                                (mapv :sg-id))]
              (if (empty? children)
                group
                (assoc group :children children))))]
    (mapv group-children data)))





