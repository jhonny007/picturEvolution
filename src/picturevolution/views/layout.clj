(ns picturevolution.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [noir.session :as session]
            [picturevolution.util :refer [gravatar-hash]]))

(defn base [& content]
  (html5
    [:head
     [:title "Welcome to picturevolution"]
     (include-css "/css/screen.css")
     (include-js "//code.jquery.com/jquery-2.0.2.min.js")]
    [:body content]))

(defn make-menu [& items]
  [:div#usermenu (for [item items] [:div.menuitem item])])

(defn guest-menu []
  (make-menu
   (link-to "/" "home")
   (link-to "/register" "register")
   (form-to [:post "/login"]
            (anti-forgery-field)
            (text-field {:placeholder "screen name"} "id")
            (password-field {:placeholder "password"} "pass")
            (submit-button "login"))))

(defn user-menu [user]
  (make-menu
   (link-to "/" "home")
   (link-to "/upload" "upload images")
   (link-to "/logout" (str "logout " user))
   
   [:img {:src (str "https://www.gravatar.com/avatar/" 
                    (if (= nil (session/get :email))
                      (gravatar-hash (session/get :user))
                      (gravatar-hash (session/get :email))
                      ) ".jpg?d=robohash")}]))

(defn common [& content]
  (base
   (if-let [user (session/get :user)]
     (user-menu user)
     (guest-menu))
   [:div.content content]))
