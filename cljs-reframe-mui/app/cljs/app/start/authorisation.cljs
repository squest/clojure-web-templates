(ns app.start.authorisation
  (:require [app.utils :as u]
            [ajax.core :as ajax]
            [cljs.reader :as reader]
            [re-frame.core :as re]
            [app.db :as db]))

(def base-url (:base-url db/default-db))

(defn silent-login
  [cred]
  (ajax/ajax-request
    {:method          :post
     :uri             (str base-url "/backsite-api/v1/user-auth/silent-login")
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :params          cred
     :handler         (fn [[status data]]
                        (if status
                          (re/dispatch [:start-event/init-user (:user-data data)])
                          (re/dispatch [:start-event/logout])))
     :error-handler   (fn [[_ msg]]
                        (do (js/alert msg)
                            (re/dispatch [:navigate :login])))}))

(defn login
  [cred]
  (ajax/ajax-request
    {:method          :post
     :uri             (str base-url "/backsite-api/v1/user-auth/login")
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :params          cred
     :handler         (fn [[status data]]
                        (if status
                          (re/dispatch [:start-event/setup-user (:user-data data)])
                          (js/alert (get-in data [:response :message]))))
     :error-handler   (fn [error]
                        (do (js/alert "Dude, you're not authorise (yet)")
                            (re/dispatch [:navigate :login])))}))

(defn signup
  [user-data]
  (ajax/ajax-request
    {:method          :post
     :uri             (str base-url "/backsite-api/v1/user-auth/signup")
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :params          user-data
     :handler         (fn [[status data]]
                        (if status
                          (re/dispatch [:start-event/setup-user (:user-data data)])
                          (js/alert (get-in data [:response :message]))))
     :error-handler   (fn [error]
                        (do (js/alert "Dude, you're not authorise (yet)")
                            (re/dispatch [:navigate :login])))}))



(defn api-health-check []
  (ajax/ajax-request
    {:method          :get
     :uri             (str base-url "/backsite-api/v1/user-auth")
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :handler         (fn [[_ data]]
                        (js/console.log data))
     :error-handler   (fn [[_ msg]]
                        (do (js/alert msg)
                            (re/dispatch [:navigate :login])))}))

(defn init-authorise []
  (api-health-check)
  (if-let [local-cred (reader/read-string (u/get-storage "cred"))]
    (re/dispatch [:main-event/set-active-view :main])
    (re/dispatch [:navigate :login])))


