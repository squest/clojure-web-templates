(ns app.user.subs
  (:require [re-frame.core :as re]))

(re/reg-sub
 :user-subs/users
 (fn [db]
   (:users db)))
