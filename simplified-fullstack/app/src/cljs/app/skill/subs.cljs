(ns app.skill.subs
  (:require [re-frame.core :as re]
            [app.utils :as u]))

(re/reg-sub
 :skill-subs/skill-groups
 (fn [db]
   (:skill-groups db)))
