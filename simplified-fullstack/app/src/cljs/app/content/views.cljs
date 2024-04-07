(ns app.content.views
  (:require
    [app.utils :as u]
    [reagent.core :as rc]
    [re-frame.core :as re]
    ["@mui/icons-material" :as icon]
    ["@mui/lab" :as mui-lab]
    ["@mui/material" :as mui]
    [app.content.view-skill :as vs]))

(defn my-icon-button []
  [:> mui/IconButton {:color "primary"}
   [:> icon/ExpandMore {:fontSize "large"}]])

(defn my-button []
  [:> mui/Button {:variant "contained" :color "primary"} "Click Me!"])


(defn content-management-page []
  [:div [:center
         [:h3 "This is content management page"]
         [vs/reorder-skills-dialog]]])

