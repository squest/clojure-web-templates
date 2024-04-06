(ns app.handler
  (:require
    [compojure.core :refer [GET POST context routes]]
    [compojure.route :refer [resources files not-found]]
    [app.utils :refer :all]
    [app.view :as view]
    [noir.response :as nresp]
    [ring.util.response :as resp]
    [cheshire.core :as cc]
    [ring.middleware.defaults :refer :all]
    [app.utils :refer :all]
    [noir.cookies :as cook]
    [noir.session :as sess]))

(declare front-routes api-routes other-routes)

;; you can pass db or anything to the routes that will be used by functions under them

(defn main-routes
  []
  (routes (front-routes)
          (context "/api" req (api-routes))
          (other-routes)))

(defn other-routes
  []
  (routes
    (resources "/")
    (not-found "<center><h1>Nothing to see here</h1></center>")))

(defn front-routes
  []
  (routes
    (GET "/" req (view/home))
    (GET "/viewer" req (view/viewer))))

(defn api-routes
  []
  (routes
    (GET "/say-hello/:name" req
      (do (pres req)
          (-> {:message (str "Hellow " (get-in req [:params :name]))}
              cc/generate-string)))))

(defn handler
  []
  (let [site-config (assoc-in site-defaults [:security :anti-forgery] false)]
    (-> (main-routes)
        cook/wrap-noir-cookies
        sess/wrap-noir-session
        (wrap-defaults site-config))))
