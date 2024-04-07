(ns app.user.events
  (:require [re-frame.core :as re]))

(re/reg-event-db
  :user-event/set-users
  (fn [db [_ users]]
    (assoc db :users users)))
