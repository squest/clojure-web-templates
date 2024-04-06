(ns app.main.events
  (:require [re-frame.core :as re]
            [app.db :as db]))

(re/reg-event-db
  :main-event/initialise-db
  (fn [_ _]
    db/default-db))

(re/reg-event-db
  :main-event/set-active-view
  (fn [db [_ active-view]]
    (assoc-in db [:views :active-view] active-view)))

(re/reg-event-db
  :main-event/set-active-page
  (fn [db [_ active-page]]
    (assoc-in db [:views :active-page] active-page)))
