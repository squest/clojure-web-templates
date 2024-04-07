(ns app.backsite.user-auth.ctrl
  (:require
    [app.utils :refer :all]
    [app.backsite.user-auth.logic :as logic]))

(defn login
  "This is the normal login controller, requires username and password in the body"
  [db req]
  (info "Masuk ke login")
  (if-let [user-cred (logic/login db (get-in req [:body]))]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    {:status    "ok"
               :message   "Login success"
               :user-data user-cred}}
    {:status  401
     :headers {"Content-Type" "application/json"}
     :body    {:status  "error"
               :message "Invalid username or password or belom diapprove"}}))

(defn silent-login
  "This is the silent login controller, requires :username :_id & :token in the body"
  [db req]
  (info "Masuk ke silent-login")
  (if-let [user-cred (logic/cred-check db (get-in req [:body]))]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    {:status    "ok"
               :message   "Login success"}}
    {:status  401
     :headers {"Content-Type" "application/json"}
     :body    {:status  "error"
               :message "Invalid username or _id or token"}}))

(defn signup
  "This is a signup controller, requires :username, :password, and :name in the body"
  [db req]
  (let [cred (logic/signup db (get-in req [:body]))]
    (info "Masuk ke signup")
    (if cred
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body    {:status    "ok"
                 :message   "Signup success tp tunggu approval dari admin"
                 :user-data cred}}
      {:status  400
       :headers {"Content-Type" "application/json"}
       :body    {:status  "error"
                 :message "Username already exists"}})))




















