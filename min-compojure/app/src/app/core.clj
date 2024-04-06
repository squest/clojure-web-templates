(ns app.core
  (:require [app.server :as web]
            [app.handler :as app-handler]
            [app.utils :refer :all]))

(defonce server (atom nil))

(defn start
  []
  (let [server-config (:server (read-config))
        handler (app-handler/handler)]
    ;; server config should consist of :port :host :path
    (->> (web/start-server handler server-config)
         (reset! server))
    (info "Server started at port " (:port server-config))))

(defn stop
  []
  (web/stop-server @server))


