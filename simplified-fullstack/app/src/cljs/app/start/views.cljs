(ns app.start.views
  (:require [reagent.core :as rc]
            [re-frame.core :as re]
            [app.start.authorisation :as auth]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.material.text-field :refer [text-field]]
            [reagent-mui.material.typography :refer [typography]]
            [reagent-mui.material.link :refer [link]]))

(declare login-page signup-page)

(defn main-page []
  (let [active-page (re/subscribe [:main-subs/active-page])]
    (case @active-page
      :login [login-page]
      :signup [signup-page])))

(defn signup-page []
  (let [name (rc/atom "")
        username (rc/atom "")
        password (rc/atom "")
        confirm-password (rc/atom "")
        password-match (rc/atom true)]
    (fn []
      [:div {:style {:display         "flex"
                     :justify-content "center"
                     :align-items     "center"
                     :height          "100vh"}}
       [:div {:style {:width         "400px"
                      :padding       "20px"
                      :border        "1px solid #ccc"
                      :border-radius "8px"}}
        [typography {:variant "h5"} "Sign Up"]
        [text-field
         {:label     "Name"
          :variant   "outlined"
          :style     {:width "100%"}
          :value     @name
          :on-change #(reset! name (-> % .-target .-value))}]
        [text-field
         {:label     "Username"
          :variant   "outlined"
          :style     {:width "100%" :margin-top "20px"}
          :value     @username
          :on-change #(reset! username (-> % .-target .-value))}]
        [text-field
         {:label     "Password"
          :variant   "outlined"
          :type      "password"
          :style     {:width "100%" :margin-top "20px"}
          :value     @password
          :on-change #(do
                        (reset! password (-> % .-target .-value))
                        (reset! password-match (= @password @confirm-password)))}]
        [text-field
         {:label       "Confirm Password"
          :variant     "outlined"
          :type        "password"
          :style       {:width "100%" :margin-top "20px"}
          :value       @confirm-password
          :on-change   #(do
                          (reset! confirm-password (-> % .-target .-value))
                          (reset! password-match (= @password @confirm-password)))
          :error       (not @password-match)
          :helper-text (when (not @password-match) "Passwords do not match.")}]
        [button
         {:variant  "contained"
          :color    "primary"
          :style    {:width "100%" :margin-top "20px"}
          :disabled (not @password-match)
          :on-click #(let [user-data {:name     @name
                                      :username @username
                                      :password @password}]
                       (js/alert user-data)
                       (auth/signup user-data))}
         "Sign Up"]
        [:div {:style {:margin-top "10px"}}
         [link {:href "/start/login"} "Dah ada account? login aja atuh"]]]])))

(defn login-page []
  (let [username (rc/atom "")
        password (rc/atom "")]
    (fn []
      [:div {:style {:display         "flex"
                     :justify-content "center"
                     :align-items     "center"
                     :height          "100vh"}}
       [:div {:style {:width         "400px"
                      :padding       "20px"
                      :border        "1px solid #ccc"
                      :border-radius "8px"}}
        [typography {:variant "h5"} "Login Page"]
        [:br]
        [text-field
         {:label     "Username"
          :variant   "outlined"
          :style     {:width "100%"}
          :value     @username
          :on-change #(reset! username (-> % .-target .-value))}]
        [text-field
         {:label     "Password"
          :variant   "outlined"
          :type      "password"
          :style     {:width "100%" :margin-top "20px"}
          :value     @password
          :on-change #(reset! password (-> % .-target .-value))}]
        [button
         {:variant  "contained"
          :color    "primary"
          :style    {:width "100%" :margin-top "20px"}
          :on-click #(auth/login {:username @username
                                  :password @password})}
         "Login"]
        [:div {:style {:margin-top "10px"}}
         [link {:href "/start/signup"} "Don't have an account? Sign up here"]]]])))


