(ns app.plumbing.routes
  (:require
    [reitit.ring :as ring]
    [app.utils :refer :all]
    [app.backsite.user-auth.routes :as user-auth]
    [app.backsite.users.routes :as users-mgt]
    [app.plumbing.midware :as midware]))

(defn backsite-api
  "APIs specifically for backoffice needs"
  [db midware]
  ["/backsite-api"
   ["/v1"
    (user-auth/routes db)
    (users-mgt/routes db midware)
    ["/health" {:get midware/api-check}]]])

(defn create-routes
  "Creates the whole routes for the system"
  [db]
  (ring/router
    [(backsite-api db midware/backware)]))
