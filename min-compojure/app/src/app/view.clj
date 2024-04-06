(ns app.view
  (:require
    [selmer.parser :as selmer :refer [render-file]]
    [app.utils :refer :all]))

(selmer/cache-off!)

(defn file
  [filename]
  (str "template/" filename ".html"))

(defn home []
  (-> (file "index")
      (render-file {:page "Home"})))

(defn viewer
  []
  "Html template producing static for the mobile version.
  It accepts the resources to be included into the html header."
  (-> (file "index")
      (render-file {:page "Viewer"})))