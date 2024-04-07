(ns dev
  (:require [com.stuartsierra.component :as component]
            [app.system :as system]
            [app.utils :refer :all]
            [clojure.tools.namespace.repl :as ns-repl]))

(defonce dev-system (atom nil))

;; create a function that reads config.edn and then get the port
;; looks like only for logging
(defn url
  "Get the port from the config.edn file"
  [path]
  (let [port (-> (read-config)
                 (get-in [:server :port]))]
    (str "http://localhost:" port "/api" path)))

;;===== SYSTEM RELATED FUNCTIONS ======

;; Create a function to restart the system

(defn start
  "Starting the webapp"
  []
  (->> (system/create-system)
       (component/start-system)
       (reset! dev-system)))

(defn stop []
  (swap! dev-system component/stop-system))

(defn restart
  []
  (do (stop)
      (ns-repl/refresh)
      (print "Restarting the system in 2 seconds... ")
      (Thread/sleep 100)
      (println "plus/minus 5 minutes.")
      (Thread/sleep 100)
      (start)))
