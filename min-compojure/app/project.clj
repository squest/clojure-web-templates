(defproject app "0.1.0"
  :dependencies
  [
   ;; basic clojure setup and app management
   [org.clojure/clojure "1.11.1"]

   ;; basic web plumbing & web needs
   [org.immutant/immutant "2.1.10"]
   ;; very old libraries, ugly but useful lah for beginners
   ;; compojure resides here
   [lib-noir "0.9.9"]
   [ring/ring-defaults "0.3.4" :exclusions [commons-codec]]
   [compojure "1.7.1"]
   [ring "1.9.6" :exclusions [commons-codec]]
   [selmer "1.12.56" :exclusions [commons-codec]]

   ;; database & data processing
   [cheshire "5.11.0"]
   ;; utilities
   [com.taoensso/timbre "6.1.0"]
   [clojure.java-time "1.2.0"]
   ;; [me.raynes/fs "1.4.6"]
   [danlentz/clj-uuid "0.1.9"]

   ;; file/formatting and development utilities
   [pjstadig/humane-test-output "0.11.0"]
   [ring/ring-mock "0.4.0"]
   ]

  :injections [(require 'pjstadig.humane-test-output)
               (pjstadig.humane-test-output/activate!)]

  :uberjar-name "uberjar-app.jar"
  :jar-name "appstore.jar"

  :min-lein-version "2.5.3"

  :source-paths ["src" "dev"]

  :resource-paths ["resources"]
  :main app.core
  :repl-options {:init-ns app.core}

  :plugins [[lein-environ "1.2.0"]]

  :profiles {:dev           [:project/dev :profiles/dev]
             :test          [:project/test :profiles/test]
             ;; only edit :profiles/* in profiles.clj
             :profiles/dev  {}
             :profiles/test {}
             :project/dev   {:source-paths ["src" "dev"]}
             :project/test  {:source-paths ["src" "dev"]}
             :uberjar       {:aot          :all
                             :source-paths ["src"]
                             :main         app.core}})