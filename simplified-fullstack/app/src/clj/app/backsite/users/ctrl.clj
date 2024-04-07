(ns app.backsite.users.ctrl
  (:require [app.utils :refer :all]
            [app.backsite.users.logic :as logic]))

(defn get-users
  "Get all users in the db, but only for selected keys"
  [db req]
  (info "Getting into get-users handler")
  (if-let [users (logic/get-users db)]
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
  (let [user-data (get-in req [:body :data])
        db-user-data (logic/update-user db user-data)]
    (if db-user-data
      {:body {:status     "ok"
              :message    "User updated"
              :users-data db-user-data}}
      {:status 500
       :body   {:status  "error"
                :message "Cannot update the user for some reason, maybe wrong id, just maybe tho"}})))
