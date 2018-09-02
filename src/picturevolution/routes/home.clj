(ns picturevolution.routes.home
  (:require [compojure.core :refer :all]
            [picturevolution.views.layout :as layout]
            [picturevolution.routes.gallery :refer [show-galleries]]
            [noir.session :as session]))

(defn home []
  (layout/common (show-galleries)))

(defroutes home-routes
  (GET "/" [] (home)))
