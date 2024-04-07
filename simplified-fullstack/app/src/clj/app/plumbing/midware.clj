(ns app.plumbing.midware
  (:require [app.utils :refer :all]
            [app.backsite.user-auth.logic :as user-auth]))

(defn api-check
  [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    {:status  "ok"
             :message "Backsite API is working"}})

(defn backware
  "Create a base-auth function to be universally used across the backsite"
  [fun db request]
  (info "=======================================================================")
  (info "URI : " (:uri request))
  (if (user-auth/cred-check db request)
    (merge {:status  200
            :headers {"Content-type" "application/json"}}
           (fun db request))
    {:status  401
     :headers {"Content-Type" "application/json"}
     :body    {:status  "error"
               :message "Failed to authenticate, please banget relogin and refresh!"}}))
