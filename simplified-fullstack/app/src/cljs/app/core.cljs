(ns app.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re]
            [app.main.events]
            [app.main.subs]
            [app.main.routes :as routes]
            [app.main.views :as views]
            [app.start.authorisation :as auth]
            [app.start.events]
            [app.user.subs]
            [app.user.events]
            [app.skill.subs]
            [app.skill.events]
            [app.content.subs]
            [app.content.events]
            [app.config :as config]
            [app.utils :as u]))

(re/reg-event-fx
  :navigate
  (fn [{:keys [db]} [_ handler]]
    {:fx [(routes/navigate! handler)]}))

(re/reg-event-fx
  :nothing
  (fn [_ _]
    {:fx []}))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-view] root-el)))

(defn init []
  (routes/start!)
  (re/dispatch-sync [:main-event/initialise-db])
  (dev-setup)
  (mount-root)
  (auth/init-authorise))
