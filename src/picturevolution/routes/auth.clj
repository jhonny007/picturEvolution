(ns picturevolution.routes.auth
   (:require [compojure.core :refer :all]
             [picturevolution.util :refer [gallery-path]]
             [picturevolution.views.layout :as layout]
             [picturevolution.routes.home :refer :all]
             [picturevolution.models.db :as db]
             [ring.util.anti-forgery :refer [anti-forgery-field]]
             [noir.response :as resp]
             [noir.session :as session]
             [noir.util.crypt :as crypt]
             [noir.validation :refer [rule errors? has-value? on-error min-length?]]
             [hiccup.form :refer :all])
   (:import java.io.File))

(defn create-gallery-path []
  (let [user-path (File. (gallery-path))]
    (if-not (.exists user-path) (.mkdirs user-path))
    (str (.getAbsolutePath user-path) File/separator)))

(defn format-error [[error]]
  [:div.error error])

(defn control [field name tabindex text]
  (list (on-error name format-error) 
        (label name text)
        (field {:tabindex tabindex} name)
        [:br]))

(defn registration-page []
  (layout/base
   (form-to [:post "/register"]
            (anti-forgery-field)
            (control text-field :id 1 "screen name")
            (control text-field :email 2 "email")
            (control password-field :pass 3 "Password")
            (control password-field :pass1 4 "Retype Password")
            (submit-button {:tabindex 5} "create account"))))

(defn handle-registration [id email pass pass1]
  (rule (min-length? pass 5)
        [:pass "password must be at least 5 characters"])
  (rule (= pass pass1) [:pass "password was not retyped correctly"])
  (if (errors? :pass)
    (registration-page)
    (do 
      (db/add-user-record {:id id :email email :pass (crypt/encrypt pass)})
      (session/put! :user id)
      (create-gallery-path)
      (resp/redirect "/")
)))


(defn login-page []
  (layout/common
   (form-to [:post "/login"]
            (anti-forgery-field)
            (control text-field :id 1 "screen name")
            (control password-field :pass 2 "Password")
            (submit-button {:tabindex 3} "login"))))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (rule (has-value? id)
          [:id "screen name is required"])
    (rule (has-value? pass)
          [:pass "password is required"])
    (rule (and user (crypt/compare pass (:pass user)))
          [:pass "invalid password"])
    (if (errors? :id :pass)
      (login-page)
      (do
        (session/put! :user id)
        (session/put! :email (:email user))
        (resp/redirect "/")))))

(defroutes auth-routes
  (GET "/register" [_] (registration-page))
  (POST "/register" [id email pass pass1] (handle-registration id email pass pass1))
  (GET "/login" [_] (login-page))
  (POST "/login" [id pass] (handle-login id pass))
  (GET "/logout" [] 
       (layout/common (form-to [:post "/logout"]
                               (anti-forgery-field)
                               (submit-button "logout"))))
  (POST "/logout" []
        (session/clear!)
        (resp/redirect "/")))
