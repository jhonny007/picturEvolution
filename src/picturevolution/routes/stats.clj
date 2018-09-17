(ns picturevolution.routes.stats
   (:require [compojure.core :refer [defroutes GET POST]]
             [picturevolution.views.layout :as layout]
             [picturevolution.models.db :as db]))

(defn display-stats []

  (let [count-users db/count-users] [:div 
           [:p "Users: ["    (:count  (db/count-users)) "]"]
           [:p "Images: [" (:count  (db/count-images)) "]"]]))

(defroutes stats-routes
  (GET "/stats" [] (layout/common
                    (display-stats))))
