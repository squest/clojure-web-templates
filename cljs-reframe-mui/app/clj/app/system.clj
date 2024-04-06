(ns app.system
  (:require
    [com.stuartsierra.component :as component]
    [app.plumbing.db :as db]
    [app.utils :refer :all]
    [app.plumbing.server :as immut]
    [app.plumbing.handler :as http]))

(defn create-system
  "It creates a system, and return the system, but not started yet"
  []
  (let [{:keys [server-path
                server-port
                server-host
                db-mongo-uri
                db-mongo-port
                db-mongo-name-1
                db-mongo-quiet
                db-mongo-debug
                db-admin-id
                token
                s3-cf-assets-url
                s3-access-key
                s3-secret-key
                s3-bucket-region
                s3-bucket-name
                zenbrain-api]} (read-config-true-flat)
        ;; {:keys [server db]} (read-config-flat)
        server {:port server-port :path server-path :host server-host}
        db-mongo-1 {:uri      db-mongo-uri
                    :port     db-mongo-port
                    :db       db-mongo-name-1
                    :quiet    db-mongo-quiet
                    :debug    db-mongo-debug
                    :admin-id db-admin-id}
        other-config {:zenbrain-api     zenbrain-api
                      :token            token
                      :s3-cf-assets-url s3-cf-assets-url
                      :s3-access-key    s3-access-key
                      :s3-secret-key    s3-secret-key
                      :s3-bucket-region s3-bucket-region
                      :s3-bucket-name   s3-bucket-name}]
    (component/system-map
      :dbase (db/create-database-component db-mongo-1 other-config db-admin-id)
      :handler (component/using (http/create-handler-component) [:dbase])
      :server (component/using (immut/create-server-component server) [:handler]))))
