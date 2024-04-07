(ns app.main.subs
  (:require [re-frame.core :as re]))

(re/reg-sub
 :main-subs/active-view
 (fn [db _]
   (get-in db [:views :active-view])))

(re/reg-sub
  :main-subs/active-page
  (fn [db _]
    (get-in db [:views :active-page])))

(re/reg-sub
  :main-subs/user
  (fn [db _]
    (get-in db [:user])))

