(ns app.backsite.user-auth.controller
  (:require
    [app.utils :refer :all]
    [monger.collection :as mc]))

(defn auth
  "Authenticating user request"
  [db request]
  (do (info "=======================================================================")
      (info "URI : " (:uri request))
      (info (str "Bo token : "
                 (= (:bo-version-token db)
                    (get-in request [:body :cred :bo-version-token])))))
  (or (= (:request-method request) :get)
      (let [user (get-in request [:body :cred])]
        (pres user)
        (let [db-user (mc/find-one-as-map (:db-1 db) "users" {:username (:username user)})]
          (info "authorising user " (:username user))
          (pres db-user)
          (when db-user
            (mc/update-by-id (:db-1 db) "users" (:_id db-user)
                             (assoc db-user :last-active (now))))
          ;; every post request must have a cred field
          ;; users cred resides in db-cred (a ref)
          ;; return true when it matches user cred in db-cred
          ;; user-cred should include :token, :_id, and :username
          (info "Pass the authentication")
          (and (:approved db-user)
               (= (select-keys user [:_id :token :username :bo-version-token])
                  (-> (select-keys db-user [:_id :token :username])
                      (merge {:bo-version-token (:bo-version-token db)}))))))))

(defn cred-check
  "Checking credentials when login"
  [db request]
  (let [cred (get-in request [:body])
        cred-from-db (mc/find-one-as-map (:db-1 db) "users" {:username (:username cred)})]
    (pres cred)
    (info "Checking credential for " (:username cred))
    (when (and cred-from-db
               (:approved cred-from-db)
               (= (cred-from-db :password) (hash (cred :password))))
      (-> (dissoc cred-from-db :password)
          (assoc :bo-version-token (:bo-version-token db))))))

(defn login
  [db req]
  (info "Masuk ke login")
  (if-let [user-cred (cred-check db req)]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    {:status    "ok"
               :message   "Login success"
               :user-data user-cred}}
    {:status  401
     :headers {"Content-Type" "application/json"}
     :body    {:status  "error"
               :message "Invalid username or password or belom diapprove"}}))

(defn signup
  "This is the signup function with a bit complex logic"
  [db req]
  (let [cred (get-in req [:body])
        cred-from-db (mc/find-one-as-map (:db-1 db) "users" {:username (:username cred)})]
    (info "Masuk ke signup")
    (if cred-from-db
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body    {:status  "error"
                 :message "Username already exists"}}
      (let [new-cred (merge cred
                            {:password         (hash (cred :password))
                             :_id              (uuid)
                             :bo-version-token (:bo-version-token db)
                             :token            (uuid)
                             :role             "content-maker"
                             :approved         false})]
        (pres (mc/insert-and-return (:db-1 db) "users" new-cred))
        (info "Insert new user ke mongo sukses")
        (info "Update creds sukses")
        {:status  200
         :headers {"Content-Type" "application/json"}
         :body    {:status    "ok"
                   :message   "Signup success tp tunggu approval dari admin"
                   :user-data (dissoc new-cred :password)}}))))




















