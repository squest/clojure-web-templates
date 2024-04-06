(ns app.user.views
  (:require [re-frame.core :as re]
            [app.user.ajax :as ajax]
            [reagent.core :as rc]
            [app.utils :as u]
            [reagent-mui.material.table :refer [table]]
            [reagent-mui.material.table-container :refer [table-container]]
            [reagent-mui.material.table-body :refer [table-body]]
            [reagent-mui.material.table-cell :refer [table-cell]]
            [reagent-mui.material.table-head :refer [table-head]]
            [reagent-mui.material.table-row :refer [table-row]]
            [reagent-mui.material.select :refer [select]]
            [reagent-mui.material.menu-item :refer [menu-item]]
            [reagent-mui.material.paper :refer [paper]]
            [reagent-mui.material.checkbox :refer [checkbox]]
            [reagent-mui.material.text-field :refer [text-field]]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.material.typography :refer [typography]]))

(defn user-management-panel-1
  [cred users]
  (let [cell-styles {:style {:width "10%"}}
        cell-styles-20 {:style {:width "20%"}}]
    [table-container
     [table {:size "small"}
      [table-head {:style {:width "100%"}}
       [table-row
        [table-cell "Nomer"]
        [table-cell "Username"]
        [table-cell "Name"]
        [table-cell "Role"]
        [table-cell "Approved"]
        [table-cell "Action"]]]
      [table-body
       (for [[index user] (map-indexed vector users)]
         ^{:key index}
         [table-row {:style {:width "100%"}}
          [table-cell (merge cell-styles {:align "center"}) [:<> (inc index)]]
          [table-cell (merge cell-styles-20 {:align "left"}) [:<> (:username user)]]
          [table-cell (merge cell-styles-20 {:align "left"}) [:<> (:name user)]]
          [table-cell (merge cell-styles-20 {:align "left"})
           [select {:value     (get-in users [index :role])
                    :on-change (fn [e] (re/dispatch
                                         [:user-event/set-users
                                          (assoc-in users [index :role]
                                                    (-> e .-target .-value))]))
                    :style     {:width "100%"}}
            (for [role-type ["admin" "manager" "content-maker"]]
              [menu-item {:value role-type :key role-type} role-type])]]
          [table-cell (merge cell-styles {:align "center"})
           [checkbox
            {:checked   (get-in users [index :approved])
             :on-change (fn [e] (re/dispatch
                                  [:user-event/set-users
                                   (assoc-in users [index :approved]
                                             (-> e .-target .-checked))])
                          (js/console.log (get-in users [index :approved])))}]]
          [table-cell (merge cell-styles-20 {:align "center"})
           [button {:variant  "contained"
                    :on-click #(when (js/confirm (str "Save this user data? " (users index)))
                                 (ajax/update-user cred (users index)))}
            "Save"]]])]]]))

(defn user-management-panel
  [cred users]
  (let [cell-styles {:style {:width "10%"}}
        cell-styles-20 {:style {:width "20%"}}]
    [table-container
     [table {:size "small"}
      [table-head {:style {:width "100%"}}
       [table-row
        [table-cell (merge cell-styles {:align "center"}) "Nomer"]
        [table-cell (merge cell-styles-20 {:align "left"}) "Username"]
        [table-cell (merge cell-styles-20 {:align "left"}) "Name"]
        [table-cell (merge cell-styles-20 {:align "left"}) "Role"]
        [table-cell (merge cell-styles {:align "center"}) "Approved"]
        [table-cell (merge cell-styles-20 {:align "center"}) "Action"]]]
      [table-body
       (for [[index user] (map-indexed vector users)]
         ^{:key index}
         [table-row {:style {:width "100%"}}
          [table-cell (merge cell-styles {:align "center"}) [:<> (inc index)]]
          [table-cell (merge cell-styles-20 {:align "left"}) [:<> (:username user)]]
          [table-cell (merge cell-styles-20 {:align "left"}) [:<> (:name user)]]
          [table-cell (merge cell-styles-20 {:align "left"})
           [select {:value     (get-in users [index :role])
                    :on-change (fn [e] (re/dispatch
                                         [:user-event/set-users
                                          (assoc-in users [index :role]
                                                    (-> e .-target .-value))]))
                    :style     {:width "100%"}}
            (for [role-type ["admin" "manager" "content-maker"]]
              [menu-item {:value role-type :key role-type} role-type])]]
          [table-cell (merge cell-styles {:align "center"})
           [checkbox
            {:checked   (get-in users [index :approved])
             :on-change (fn [e] (re/dispatch
                                  [:user-event/set-users
                                   (assoc-in users [index :approved]
                                             (-> e .-target .-checked))])
                          (js/console.log (get-in users [index :approved])))}]]
          [table-cell (merge cell-styles-20 {:align "center"})
           [button {:variant  "contained"
                    :on-click #(when (js/confirm (str "Save this user data? " (users index)))
                                 (ajax/update-user cred (users index)))}
            "Save"]]])]]]))

(defn user-management-page []
  (let [users (re/subscribe [:user-subs/users])
        cred (re/subscribe [:main-subs/user])]
    (if (or (nil? @users) (empty? @users))
      (do (ajax/get-users @cred)
          [:center [:h4 "Loading..."]])
      [user-management-panel @cred @users])))




