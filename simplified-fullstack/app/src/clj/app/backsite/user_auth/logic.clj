(ns app.backsite.user-auth.logic
  (:require [app.utils :refer :all]
            [monger.collection :as mc]))

(defn cred-check
  "Authenticate user, used each time user tries to access a route that needs authentication"
  [db user-data]
  (info "Getting into auth logic")
  (let [user-db (mc/find-map-by-id (:db db) "users" (:_id user-data))
        res (= (:approved user-db)
               (select-keys user-data [:_id :username :token])
               (select-keys user-db [:_id :username :token]))]
    (when res
      (do (info "User authenticated")
          (mc/update-by-id (:db db) "users" (:_id user-data)
                           (assoc user-data :last-active (now)))
          (select-keys user-db [:_id :username :token])))))

(defn login
  "Logic to handle normal user login"
  [db {:keys [username password] :as user-data}]
  (info "Getting into login logic")
  (let [db-user (mc/find-one-as-map (:db db) "users" {:username username})
        res (and db-user
                 (= (hash password) (db-user :password))
                 (db-user :approved))]
    (when res
      (do (info "User logged in")
          (mc/update-by-id (:db db) "users" (:_id db-user)
                           (assoc db-user :last-active (now)))
          (select-keys db-user [:_id :username :token])))))

(defn signup
  "Logic to handle user signup"
  [db user-data]
  (info "Getting into signup logic")
  (let [db-user (mc/find-one-as-map (:db db) "users" {:username (user-data :username)})]
    (when-not db-user
      (let [new-user (merge user-data
                            {:password (hash (user-data :password))
                             :_id      (uuid)
                             :role     "content-maker"
                             :approved false})]
        (do (mc/insert-and-return (:db db) "users" new-user)
            (info "New user inserted to db")
            (select-keys new-user [:_id :username :token]))))))





