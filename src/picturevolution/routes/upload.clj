(ns picturevolution.routes.upload
   (:require [compojure.core :refer [defroutes GET POST]]
             [picturevolution.util :refer [galleries gallery-path thumb-prefix thumb-uri]]
             [picturevolution.views.layout :as layout]
             [picturevolution.routes.home :refer :all]
             [picturevolution.models.db :as db]
             [noir.io :refer [upload-file]]
             [noir.response :as resp]
             [noir.session :as session]
             [noir.util.route :refer [restricted]]
             [noir.validation :refer [rule errors? has-value? on-error min-length?]]
             [hiccup.form :refer :all]
             [hiccup.element :refer [image]]
             [hiccup.util :refer [url-encode]]
             [clojure.java.io :as io]
             [ring.util.anti-forgery :refer [anti-forgery-field]]
             [ring.util.response :refer [file-response]])
   (:import [java.io File FileInputStream FileOutputStream]
            [java.awt.image AffineTransformOp BufferedImage]
            java.awt.RenderingHints
            java.awt.geom.AffineTransform
            javax.imageio.ImageIO))

(def thumb-size-small 150)
(def thumb-size-medium 300)
(def thumb-size-big 600)

(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance 
               (double ratio) (double ratio))

        transform-op (AffineTransformOp. scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file]
  (let [img          (ImageIO/read file)
        img-width    (.getWidth img)
        img-height   (.getHeight img)
        ratio        (/ thumb-size-small img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size-small)))

(defn save-thumbnail [{:keys [filename]}]
  (let [path (str (gallery-path) File/separator)]
    (ImageIO/write
     (scale-image (io/input-stream (str path filename)))
     "jpeg"
     (File. (str path thumb-prefix filename)))))

(defn upload-page [info]
  (layout/common
   [:h2 "Upload an image"]
   [:p info]
   (form-to {:enctype "multipart/form-data"}
            [:post "/upload"]
            (anti-forgery-field)
            (file-upload :file)
            (label "second-artist" "Second Artist (if any)")
            (text-field "second-artist")
            (submit-button "upload"))))

(defn handle-upload [{:keys [filename] :as file} second-artist]
  (println file)
  (upload-page 
   (if (empty? filename)
     "please select a file to upload"

     (try
       (upload-file (gallery-path) file :create-path? true)
       (save-thumbnail file)
       (db/add-image (session/get :user) filename second-artist)
       (image {:height "150px"}
              (thumb-uri (session/get :user) filename))
       (catch Exception ex
         (str "error uploading file " (.getMessage ex)))))))

(defn serve-file [user-id file-name]
  (file-response (str galleries File/separator user-id File/separator file-name)))

(defn delete-image [userid name]
  (try 
    (db/delete-image userid name)
    (io/delete-file (str (gallery-path) File/separator name))
    (io/delete-file (str (gallery-path) File/separator thumb-prefix name))
    "ok"
    (catch Exception ex (.getMessage ex))))

(defn delete-images [names]
  (println names)
  (let [userid (session/get :user)]
    (resp/json
     (for [name names] {:name name :status (delete-image userid name)}))))

(defroutes upload-routes
  (GET "/img/:user-id/:file-name" [user-id file-name] (serve-file user-id file-name))
  (GET "/upload" [info] (restricted (upload-page info)))
  (POST "/upload" [file second-artist]  (restricted (handle-upload file second-artist)))
  (POST "/delete" [names]  (restricted (delete-images names))))

