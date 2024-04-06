(ns app.plumbing.routes
  (:require
    [reitit.ring :as ring]
    [app.utils :refer :all]
    [app.backsite.user-auth.routing :as user-auth]
    [app.backsite.users.routing :as users-mgt]
    [app.backsite.user-auth.controller :as user-auth-ctrl]))

(defn backware
  "Create a base-auth function to be universally used across the backsite"
  [fun db request]
  (info "=======================================================================")
  (info "URI : " (:uri request))
  (if (user-auth-ctrl/auth db request)
    (merge {:status  200
            :headers {"Content-type" "application/json"}}
           (fun db request))
    {:status  401
     :headers {"Content-Type" "application/json"}
     :body    {:status  "error"
               :message "Failed to authenticate, please banget relogin and refresh!"}}))

(defn token-request
  "Checking the api"
  [db request]
  (info "=======================================================================")
  (info "URI : " (:uri request))
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    {:status           "ok"
             :message          "API is working"
             :bo-version-token (:bo-version-token db)}})

(defn backsite-api
  "APIs specifically for backoffice needs"
  [db midware]
  ["/backsite-api"
   ["/v1"
    (user-auth/routes db)
    (users-mgt/routes db midware)
    ["/health" {:get (partial token-request db)}]]])

(defn create-routes
  "Creates the whole routes for the system"
  [db]
  (ring/router
    [(backsite-api db backware)]))
