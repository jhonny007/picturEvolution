(ns picturevolution.routes.gallery
   (:require [compojure.core :refer [defroutes GET POST]]
             [picturevolution.util :refer [galleries gallery-path thumb-prefix thumb-uri image-uri]]
             [picturevolution.views.layout :as layout]
             [picturevolution.models.db :as db]
             [noir.response :as resp]
             [noir.session :as session]
             [noir.util.route :refer [restricted]]
             [noir.validation :refer [rule errors? has-value? on-error min-length?]]
             [hiccup.form :refer [check-box]]
             [hiccup.element :refer [image]]
             [hiccup.util :refer [url-encode]]
             [hiccup.page :refer :all]
             [clojure.java.io :as io]
             [ring.util.anti-forgery :refer [anti-forgery-field]]
             [ring.util.response :refer [file-response]]))



(defn thumbnail-link [{:keys [userid name secondartist galleryowner]}]
  (let [logged-in-user (session/get :user)]
    [:div.thumbnail
     [:a {:href (image-uri userid name)}
      (image (thumb-uri userid name))
      (if (= userid logged-in-user) (check-box name))]
     (if-not (nil? secondartist) 
       [:div#artist
        [:a {:href (str "/gallery/"  (if (= galleryowner userid) secondartist userid))}
         (if (= galleryowner userid) secondartist userid)]])]))

(defn gallery-link [{:keys [userid name]}]
  [:div.thumbnail
   [:a {:href (str "/gallery/" userid)}
    (image (thumb-uri userid name))
    userid "'s gallery"]])

(defn display-gallery [userid]
  (if-let [gallery (not-empty (map thumbnail-link (db/images-by-user userid)))]
    [:div
     [:div#error]
     gallery
     (if (= userid (session/get :user))
       [:input#delete {:type "submit" :value "delete images"}])
     ]
    [:p "The user " userid " does not have any galleries"]))

(defn show-galleries []
  (map gallery-link (db/get-gallery-previews)))

(defroutes gallery-routes
  (GET "/gallery/:userid" [userid] (layout/common
                                    (include-js "/js/gallery.js")
                                    (display-gallery userid))))

