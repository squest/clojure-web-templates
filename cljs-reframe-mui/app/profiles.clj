{:dev  {:env {:server-port              8000
              :server-path              "/"
              :server-host              "localhost"
              ;; :db-mongo-uri             "mongodb://localhost:27017/"
              :db-mongo-uri             "mongodb+srv://zenius-admin:NXweb4PwhISC4o85@zenius-staging-jgydr.mongodb.net/admin?authSource=admin&replicaSet=zenius-staging-shard-0&readPreference=primary&ssl=true"
              :db-mongo-port            27017
              ;; :db-mongo-name            "zp-alfa"
              :db-mongo-name-1          "znet-v4-academic"
              :db-mongo-quiet           true
              :db-mongo-debug           false
              :db-admin-id              "91437070604511ee90728aa153a8f1a1"
              :adaptive-api             "https://zenpro-x-adaptive-engine-staging.zeniusxyz.com"
              :token                    "5a044c601fef11eebdf99a827e2aa88b"
              :zenbrain-api             "https://api-stg.zenbrain.xyz"}}
 :test {:env {:server-port              8080
              :server-path              "/"
              :server-host              "localhost"
              :db-mongo-uri             "mongodb+srv://squest:Mizone11@zp1.nz32xyq.mongodb.net/?retryWrites=true&w=majority"
              :db-mongo-port            27017
              :db-mongo-name            "zp-test"
              :db-mongo-quiet           true
              :db-mongo-debug           false
              :db-trash-content-folders "5f9f1b0b0b9b4b0004b0b0b0"
              :db-trash-skill-groups    "5f9f1b0b0b9b4b0004b0b0b1"
              :db-admin-id              "64d41240e82411edaf17c2af0f46ccff"}}}
