(ns app.backsite.users.routing
  (:require [app.backsite.users.controller :as ctrl]
            [app.utils :refer :all]))

(defn health-check
  [req]
  (info "=======================================================================")
  (info "URI : " (:uri req))
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    {:status  "ok"
             :message "User API is working"}})

(defn routes
  "Backsite routes for users"
  [db midware]
  ["/users"
   ["" {:get health-check}]
   ["/get-users" {:post (partial midware ctrl/get-users db)}]
   ["/update-user" {:post (partial midware ctrl/update-user db)}]])

