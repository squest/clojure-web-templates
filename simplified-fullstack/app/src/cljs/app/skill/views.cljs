(ns app.skill.views
  (:require
    [reagent.core :as rc]
    [re-frame.core :as re]
    [reagent-mui.material.icon-button :refer [icon-button]]
    [reagent-mui.icons.expand-more :refer [expand-more]]
    [reagent-mui.icons.chevron-right :refer [chevron-right]]
    [reagent-mui.material.grid :refer [grid]]
    [reagent-mui.lab.tree-item :refer [tree-item]]
    [reagent-mui.lab.tree-view :refer [tree-view]]
    [reagent-mui.material.typography :refer [typography]]
    [reagent-mui.material.icon-button :refer [icon-button]]
    ["@mui/icons-material" :as icon]))

(def active-node (rc/atom ""))

(defn item-tree
  [name]
  [:div
   [typography {:variant "body2"} name]
   [icon-button {:on-click #(js/alert "Tombol Plus di-klik!")
                 :icon     "+"}]
   [icon-button {:on-click #(js/alert "Edit Name di-klik!")
                 :icon     "edit-name"}]])


(defn render-tree-node [node]
  (let [{:keys [name children id]} node]
    [:<> ^{:key id}
     [tree-item {:nodeId   id
                 :label    [item-tree name]
                 :on-click #(reset! active-node name)}
      (map render-tree-node children)]]))

(defn skill-management-page []
  (let [skill-groups (re/subscribe [:skill-subs/skill-groups])]
    (do (println "skill-groups: " @skill-groups)
        [grid {:container true :spacing 1}
         [grid {:item true :xs 4}
          [:div
           [:h1 "Skill Management"]
           [:h4 (str "Active skill group : " @active-node)]
           [tree-view {:default-expanded      ["1"]
                       :default-collapse-icon (rc/as-element [icon-button [expand-more]])
                       :default-expand-icon   (rc/as-element [icon-button [chevron-right]])
                       :default-end-icon      (rc/as-element [icon-button [:> icon/CheckBoxOutlined]])}
            (map render-tree-node @skill-groups)]]]
         [grid {:item true :xs 8}
          [:h1 "Skill group detail"]
          [:br] [:br]
          [:h4 @active-node]]])))
