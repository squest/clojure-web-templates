(ns app.server
  (:require [immutant.web :as web]
            [immutant.util :as log]))

(defn start-server
  [handler {:keys [port host path]}]
  (log/set-log-level! :OFF)
  (web/run handler {:port port :host host :path path}))

(defn stop-server
  [server]
  (web/stop server))