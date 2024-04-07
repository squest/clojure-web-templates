(ns app.backsite.user-auth.routes
  (:require [app.backsite.user-auth.ctrl :as ctrl]
            [app.utils :refer :all]))

(defn health-check
  [req]
  (info "=======================================================================")
  (info "URI : " (:uri req))
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body {:status "ok"
          :message "User API is working"}})

(defn routes
  "Backsite routes for users"
  [db]
  ["/user-auth"
   ["" {:get health-check}]
   ["/login" {:post (partial ctrl/login db)}]
   ["/signup" {:post (partial ctrl/signup db)}]
   ["/silent-login" {:post (partial ctrl/silent-login db)}]])
