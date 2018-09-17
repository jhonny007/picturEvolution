(ns picturevolution.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [picturevolution.routes.auth :refer [auth-routes]]
            [picturevolution.routes.home :refer [home-routes]]
            [picturevolution.routes.upload :refer [upload-routes]]
            [picturevolution.routes.gallery :refer [gallery-routes]]
            [picturevolution.routes.stats :refer [stats-routes]]
            [noir.util.middleware :as noir-middleware]

            [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.session.memory :refer [memory-store]]
            [noir.session :as session]
            [noir.validation :refer [wrap-noir-validation]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]

))

(defn init []
  (println "picturevolution is starting"))

(defn destroy []
  (println "picturevolution is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn user-page [_]
  (session/get :user))


(def app
  (noir-middleware/app-handler [auth-routes 
                                home-routes 
                                upload-routes
                                gallery-routes
                                stats-routes
                                app-routes]
                               :access-rules [user-page]))

;;
;;(def app
;;  (-> (handler/site
;;      (routes
;;        auth-routes
;;        home-routes
;;        upload-routes
;;        app-routes))
;;      (session/wrap-noir-session
;;       {:store (memory-store)})
;;      (wrap-noir-validation)))
