(ns picturevolution.routes.home
  (:require [compojure.core :refer :all]
            [picturevolution.views.layout :as layout]
            [picturevolution.routes.gallery :refer [show-galleries]]
            [picturevolution.routes.gallery :refer [show-downloads]]
            [noir.session :as session]))

(defn home []
  (layout/common
   (show-downloads)
   (show-galleries)))

(defroutes home-routes
  (GET "/" [] (home)))
