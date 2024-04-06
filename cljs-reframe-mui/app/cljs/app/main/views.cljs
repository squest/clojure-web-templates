(ns app.main.views
  (:require [re-frame.core :as re]
            [reagent.core :as rc]
            [app.start.views :as view-login]
            [app.skill.views :as view-skill]
            [app.content.views :as view-content]
            [app.user.views :as view-user]
            [app.user.ajax :as ajax-user]
            [reagent-mui.material.text-field :refer [text-field]]
            [reagent-mui.material.icon-button :refer [icon-button]]
            [reagent-mui.icons.minimize :refer [minimize]]
            [reagent-mui.icons.maximize :refer [maximize]]
            [reagent-mui.icons.expand-sharp :refer [expand-sharp]]
            [reagent-mui.icons.expand-more :refer [expand-more]]
            [reagent-mui.icons.chevron-right :refer [chevron-right]]
            [reagent-mui.material.svg-icon :refer [svg-icon]]
            [reagent-mui.material.app-bar :refer [app-bar]]
            [reagent-mui.material.container :refer [container]]
            [reagent-mui.material.box :refer [box]]
            [reagent-mui.material.grid :refer [grid]]
            [reagent-mui.colors :as colors]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.material.chip :refer [chip]]
            [reagent-mui.material.css-baseline :refer [css-baseline]]
            [reagent-mui.icons.minimize-two-tone :refer [minimize-two-tone]]
            [reagent-mui.material.input-adornment :refer [input-adornment]]
            [reagent-mui.icons.menu :refer [menu]]
            [reagent-mui.lab.tree-item :refer [tree-item]]
            [reagent-mui.lab.tree-view :refer [tree-view]]
            [reagent-mui.material.menu-item :refer [menu-item]]
            [reagent-mui.material.stack :refer [stack]]
            [reagent-mui.material.toolbar :refer [toolbar]]
            [reagent-mui.material.typography :refer [typography]]
            [reagent-mui.icons.add-box :refer [add-box]]
            [reagent-mui.icons.clear :refer [clear]]
            [reagent-mui.icons.logout :refer [logout]]
            [reagent-mui.icons.face :refer [face]]
            [reagent-mui.icons.face-outlined :refer [face-outlined]]
            [reagent-mui.styles :as styles]
            [reagent-mui.material.drawer :refer [drawer]]
            [reagent-mui.material.icon-button :refer [icon-button]]))

(def drawer-open? (rc/atom false))

(defn- app-drawer []
  (let [cred (re/subscribe [:main-subs/user])]
    (when @drawer-open?
      [drawer {:variant  "temporary"
               :anchor   "left"
               :open     true
               :on-close #(reset! drawer-open? false)
               :style    (merge {:marginTop "64px", :zIndex "1300"}
                                {:transition "all 0.3s ease-in-out"})}
       [:div
        [toolbar]
        [menu-item {:on-click #(re/dispatch [:navigate :home])}
         [face]
         [typography {:variant "h6"} "Home of the brave"]]
        [menu-item {:on-click #(re/dispatch [:navigate :content-management])}
         [add-box]
         [typography {:variant "h6"} "Content Management"]]
        [menu-item {:on-click #(re/dispatch [:navigate :skill-management])}
         [add-box]
         [typography {:variant "h6"} "Skill Management"]]
        [menu-item {:on-click #(re/dispatch-sync [:navigate :user-management])}
         [face-outlined]
         [typography {:variant "h6"} "User Management"]]]])))

(defn- app-header []
  [:<>
   [app-bar {:position "fixed"
             :style    {:zIndex "1600"}}                    ;; tambahin zIndex
    [toolbar
     [icon-button {:edge       "start"
                   :color      "inherit"
                   :aria-label "menu"
                   :on-click   #(reset! drawer-open? (not @drawer-open?))}
      [menu]]
     [typography {:variant "h5"} "Zenius.net academic backoffice 4.0"]
     [icon-button {:color    "inherit"
                   :style    {:margin-left "auto"}
                   :on-click #(when (js/confirm "Logging out??")
                                (re/dispatch [:start-event/logout]))}
      [logout]]]]])

(defn- app-footer []
  [:div
   [:center
    [grid {:width "95%"}
     [:hr]
     [:h4 "Copyright Zenius Education"]]]])

(def custom-theme
  {:palette {:primary   colors/indigo
             :secondary colors/yellow}})

(comment
  (def classes (let [prefix "rmui-example"]
                 {:root       (str prefix "-root")
                  :button     (str prefix "-button")
                  :text-field (str prefix "-text-field")}))

  (defn custom-styles [{:keys [theme]}]
    (let [spacing (:spacing theme)]
      {(str "&." (:root classes))        {:margin-left (spacing 8)
                                          :align-items :flex-start}
       (str "& ." (:button classes))     {:margin (spacing 1)}
       (str "& ." (:text-field classes)) {:width        200
                                          :margin-left  (spacing 1)
                                          :margin-right (spacing 1)}})))

(defn home-page []
  (let [user (re/subscribe [:main-subs/user])]
    [grid {:container true :spacing 1}
     [grid {:item true :xs 12}
      [:div
       [:h2 (str "Hi there " (:name @user) "!")]
       [:h4 "This is just a fancy way to show that the system knows you"]]]]))

(defn main-page []
  (let [active-page (re/subscribe [:main-subs/active-page])]
    [:<>
     [app-header]
     [box {:component "main"
           :sx        {:flexGrow 0 :p 4}
           :style     {:marginTop "64px"}}
      [grid {:container true :spacing 3}
       [grid {:item true :xs 12}
        (case @active-page
          :home [home-page]
          :content-management [view-content/content-management-page]
          :skill-management [view-skill/skill-management-page]
          :user-management [view-user/user-management-page])]]]
     [app-drawer]
     [app-footer]]))

(defn init-page []
  [:div [:center [:h3 "Loading..., we're unsure how long tho"]]])

(defn main-view []
  (let [active-view (re/subscribe [:main-subs/active-view])]
    [:<>
     [css-baseline]
     [styles/theme-provider (styles/create-theme custom-theme)
      (case @active-view
        :init [init-page]
        :login-signup [view-login/main-page]
        :main [main-page]
        [:center [:h3 "Loading..., we're unsure how long tho"]])]]))


