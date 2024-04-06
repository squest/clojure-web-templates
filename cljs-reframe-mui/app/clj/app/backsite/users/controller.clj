(ns app.backsite.users.controller
  (:require [app.utils :refer :all]
            [monger.collection :as mc]))

(defn get-users-logic
  [db]
  (info "Getting into get-users-logic")
  (mc/find-maps (:db-1 db) "users" {} [:_id :username :name :role :approved]))

(defn get-users
  "Get all users in the db, but only for selected keys"
  [db req]
  (info "Getting into get-users handler")
  (if-let [users (get-users-logic db)]
    (do (info "Getting into get-users")
        {:body {:status     "ok"
                :message    "All users found"
                :users-data users}})
    {:status 500
     :body   {:status  "error"
              :message "DB throw an error, we don't know why"}}))

(defn update-user
  "Update a user based on data given in request"
  [db req]
  (info "Getting into update-user handler")
  (let [user-data (get-in req [:body :user-data])
        user-id (get-in user-data [:_id])
        db-user-data (mc/find-map-by-id (:db-1 db) "users" user-id)]
    (if (->> (merge db-user-data user-data)
             (mc/update-by-id (:db-1 db) "users" user-id))
      {:body {:status     "ok"
              :message    "User updated"
              :users-data (get-users-logic db)}}
      {:status 500
       :body   {:status  "error"
                :message "Cannot update the user for some reason, maybe wrong id, just maybe tho"}})))
