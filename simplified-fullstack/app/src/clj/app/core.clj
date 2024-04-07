(ns app.core
  (:require
   [app.system :as system]
   [com.stuartsierra.component :as component]
   [app.utils])
  (:gen-class))

(defonce system (atom nil))

(defn start
  "Starting the webapp"
  []
  (->> (system/create-system)
       (component/start-system)
       (reset! system)))

(defn -main
  "starts service"
  [& args]
  (do (println (app.utils/read-config-true-flat))
      (start)))
