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



(defn thumbnail-link [{:keys [user_uuid name secondartist_user_uuid galleryowner]}]
  (let [logged-in-user (session/get :user)]
    [:div.thumbnail
     (image (thumb-uri user_uuid name))
     
     [:span.disabled.glyphicon.glyphicon-star {:aria-hidden "true"}]
     [:span.glyphicon.glyphicon-star {:aria-hidden "true"}]
     [:span.glyphicon.glyphicon-star {:aria-hidden "true"}]
     [:span.glyphicon.glyphicon-star {:aria-hidden "true"}]
     [:span.glyphicon.glyphicon-star-empty {:aria-hidden "true"}]
     
     (if (= user_uuid logged-in-user)
       [:a {:href (image-uri user_uuid name)} "Original" ;; (check-box name)
        ]

     (if (= {:count 1} (db/has-purchased logged-in-user user_uuid name) )
       [:a {:href (image-uri user_uuid name)} "Purchased"]
       [:a {:href (image-uri user_uuid name)} "Buy"])

)     
     (if-not (nil? secondartist_user_uuid) 
       [:div#artist
        [:a {:href (str "/gallery/"  (if (= galleryowner user_uuid) secondartist_user_uuid user_uuid))}
         (if (= galleryowner user_uuid) secondartist_user_uuid user_uuid)]])]))

(defn gallery-link [{:keys [user_uuid name]}]
  [:div.thumbnail
   [:a {:href (str "/gallery/" user_uuid)}
    (image (thumb-uri user_uuid name))
    user_uuid "'s gallery"]])

(defn download-link [full-size thumb]
  [:div.thumbnail
   (image thumb)
   [:a {:href full-size} "Free Download"]])

(defn display-gallery [user_uuid]
  (if-let [gallery (not-empty (map thumbnail-link (db/images-by-user user_uuid)))]
    [:div
     [:div#error]
     gallery
     (if (= user_uuid (session/get :user))
       [:input#delete {:type "submit" :value "delete images"}])
     ]
    [:p "The user " user_uuid " does not have any galleries"]))

(defn show-downloads []
  (download-link "/img/1_V1.x.pdf" "/img/1_V1.jpg"))

(defn show-galleries []
  (map gallery-link (db/get-gallery-previews)))

(defroutes gallery-routes
  (GET "/gallery/:user_uuid" [user_uuid] (layout/common
                                    (include-js "/js/gallery.js")
                                    (display-gallery user_uuid))))

