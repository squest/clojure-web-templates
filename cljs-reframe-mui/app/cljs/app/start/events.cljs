(ns app.start.events
  (:require [re-frame.core :as re]
            [app.start.authorisation :as auth]
            [app.utils :as u]))

(re/reg-event-fx
  :start-event/init-user
  (fn [{:keys [db]} [_ user-data]]
    {:db       (assoc db :user user-data)
     :dispatch (if (= :init (get-in db [:views :active-view]))
                 [:navigate :home]
                 [:nothing])}))

(re/reg-event-fx
  :start-event/silent-login
  (fn [_ [_ cred]]
    {:fx [(auth/silent-login cred)]}))

(re/reg-event-fx
  :start-event/login
  (fn [_ [_ cred]]
    {:fx [(auth/login cred)]}))

(re/reg-event-fx
  :start-event/setup-user
  (fn [{:keys [db]} [_ user-data]]
    {:db       (assoc db :user user-data)
     :dispatch [:navigate :home]
     :fx       [(u/set-storage "cred" (prn-str user-data))]}))

(re/reg-event-fx
  :start-event/logout
  (fn [{:keys [db]} _]
    {:db       (dissoc db :user)
     :dispatch [:navigate :login]
     :fx       [(u/remove-storage "cred")]}))