(ns app.backsite.users.logic
  (:require [app.utils :refer :all]
            [monger.collection :as mc]))

(defn get-users
  "Get all users in the db, but only for selected keys"
  [db]
  (info "Getting into get-users-logic")
  (mc/find-maps (:db db) "users" {} [:_id :username :name :role :approved]))

(defn update-user
  "Update a user based on data given in user-data"
  [db user-data]
  (info "Getting into update-user-logic")
  (when-let [db-user (mc/find-map-by-id (:db db) "users" (:_id user-data))]
    (do (info "User found")
        (mc/update-by-id (:db db) "users" (:_id user-data)
                         (select-keys user-data [:name :role :approved]))
        (select-keys db-user [:_id :username :name :role :approved]))))
