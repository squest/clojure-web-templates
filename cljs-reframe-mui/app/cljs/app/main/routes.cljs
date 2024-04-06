(ns app.main.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as re]))

(def routes
  (atom
    ["/" {"main"  {"/"                   :home
                   "/skill-management"   :skill-management
                   "/content-management" :content-management
                   "/user-management"    :user-management}
          "start" {"/login"  :login
                   "/signup" :signup}
          ""      :init}]))

(defn parse
  [url]
  (bidi/match-route @routes url))

(defn url-for
  [& args]
  (apply bidi/path-for (into [@routes] args)))

(defn dispatch
  [route]
  (let [target (:handler route)]
    (js/console.log "Dispatching to " target)
    (cond (= target :init)
          (re/dispatch [:main-event/set-active-view :init])
          (#{:login :signup} target)
          (do (re/dispatch [:main-event/set-active-view :login-signup])
              (re/dispatch [:main-event/set-active-page target]))
          :else (do (re/dispatch [:main-event/set-active-view :main])
                    (re/dispatch [:main-event/set-active-page target])))))

(defonce history (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (pushy/set-token! history (url-for handler)))

(defn start!
  []
  (pushy/start! history))
