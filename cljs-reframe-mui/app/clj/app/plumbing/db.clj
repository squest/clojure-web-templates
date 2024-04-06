(ns app.plumbing.db
  (:require [com.stuartsierra.component :as component]
            [monger.collection :as mc]
            [app.utils :refer :all]
            [monger.core :as mg]))

(declare init-trash clear-db init-admin)

(defrecord Dbase [config other-config admin-id]
  component/Lifecycle
  (start [this]
    ;; (pres config)
    (let [conn-1 (mg/connect config)
          db-instance-1 (mg/get-db conn-1 (:db config))]
      ;; just in case need some clearance
      ;; (clear-db db-instance)
      (let [{token            :token
             s3-access-key    :s3-access-key
             s3-secret-key    :s3-secret-key
             s3-bucket-region :s3-bucket-region
             s3-bucket-name   :s3-bucket-name
             s3-cf-assets-url :s3-cf-assets-url
             zenbrain-api     :zenbrain-api} other-config
            updated-component (assoc this
                                :db-1 db-instance-1
                                :bo-version-token (uuid)
                                :zenbrain-api zenbrain-api
                                :token token
                                :s3 {:credentials {:access-key s3-access-key
                                                   :secret-key s3-secret-key
                                                   :region     s3-bucket-region}
                                     :bucket-name s3-bucket-name
                                     :assets-url  s3-cf-assets-url})]
        ;; to initially populate the db with an admin user
        ;; (init-admin updated-component "admin")
        updated-component)))
  (stop [this]
    (when-let [conn (:conn this)]
      (mg/disconnect conn))
    (dissoc this :conn)))

(defn create-database-component [db-config other-config admin-id]
  (map->Dbase {:config       db-config
               :other-config other-config
               :admin-id     admin-id}))

(defn clear-db
  "Dropping the database"
  [db]
  (mc/remove db "content-folders" {})
  (mc/remove db "contents" {})
  (mc/remove db "notes" {})
  (mc/remove db "templates" {})
  (mc/remove db "sequences" {})
  (mc/remove db "skill-groups" {})
  (mc/remove db "skills" {})
  (mc/remove db "rel-content-skill" {})
  (mc/remove db "rel-skill-group-skill" {})
  (mc/remove db "users" {}))

(defn init-admin
  "Initiate an admin user"
  [db password]
  (let [admin-id (:admin-id db)
        admin-data {:username "admin"
                    :password (hash password)
                    :role     "admin"
                    :_id      admin-id
                    :approved true
                    :token    (uuid)
                    :name     "admin"}]
    (mc/insert (:db-1 db) "users" admin-data)))





