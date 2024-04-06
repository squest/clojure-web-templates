(ns app.plumbing.db
  (:require [com.stuartsierra.component :as component]
            [app.utils :refer :all]
            [monger.core :as mg]))

(declare init-trash clear-db init-admin)

(defrecord Dbase [config]
  component/Lifecycle
  (start [this]
    ;; (pres config)
    (let [conn (mg/connect config)
          db-instance (mg/get-db conn (:db config))]
      ;; just in case need some clearance
      ;; (clear-db db-instance)
      (let [updated-component (assoc this
                                :db db-instance
                                :conn conn)]
        ;; to initially populate the db with an admin user
        ;; (init-admin updated-component "admin")
        updated-component)))
  (stop [this]
    (when-let [conn (:conn this)]
      (mg/disconnect conn))
    (dissoc this :conn)))

(defn create-database-component [db-config]
  (map->Dbase {:config       db-config}))