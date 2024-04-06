(ns app.user.ajax
  (:require [ajax.core :as ajax]
            [re-frame.core :as re]
            [app.db :as db]))

(defn get-users [cred]
  (ajax/ajax-request
    {:method          :post
     :uri             (str (:base-url db/default-db) "/backsite-api/v1/user/get-users")
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :params          {:cred cred}
     :handler         (fn [[status data]]
                        (if status
                          (re/dispatch [:user-event/set-users (:users data)])
                          (js/alert (get-in data [:response :message]))))
     :error-handler   (fn [error]
                        (do (js/alert "Dude, you're not authorise (yet)")
                            (re/dispatch [:navigate :login])))}))

(defn update-user [cred user-data]
  (ajax/ajax-request
    {:method          :post
     :uri             (str (:base-url db/default-db) "/backsite-api/v1/user/update-user")
     :mode            :no-cors
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :params          {:cred      cred
                       :user-data user-data}
     :handler         (fn [[status data]]
                        (if status
                          (re/dispatch [:user-event/set-users (:users data)])
                          (js/alert (get-in data [:response :message]))))
     :error-handler   (fn [error]
                        (do (js/alert "Dude, you're not authorise (yet)")
                            (re/dispatch [:navigate :login])))}))


