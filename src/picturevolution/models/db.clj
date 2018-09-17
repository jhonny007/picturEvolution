(ns picturevolution.models.db
  (:require [clojure.java.jdbc :as sql]
            [pg-types.all]))

(def db
  {:subprotocol "postgresql"
   :subname "//localhost/picturevolution"
   :user "admin"
   :password "admin"})


(defn add-user-record [user]
  (sql/insert! db :users user))

(defn get-user [id]
  (sql/query db
             ["SELECT * from users where id = ?" id]
             {:result-set-fn first}))

(defn get-users []
  (sql/query db
             ["SELECT * from users"]
             {:result-set-fn doall}))

(defn count-users []
  (sql/query db
             ["SELECT count(*) as count from users"]
             {:result-set-fn first}))

(defn add-image [user_uuid name secondartist_user_uuid]
;; do in a transaction
    (let [result (sql/query
                  db
                  ["select user_uuid from images where user_uuid =? and name = ?" user_uuid name])]
      (println result)
      (if (empty? result)
        
        (sql/insert! db :images {:user_uuid user_uuid :name name :secondartist_user_uuid  (if (= 0 (.length secondartist_user_uuid)) nil secondartist_user_uuid)})
        (throw
         (Exception. "you have already uploaded an image with the same name")))))

(defn images-by-user [user_uuid]
  (sql/query db
             ["select ?::uuid as galleryowner, * from images where user_uuid = ? or secondartist_user_uuid = ?" user_uuid user_uuid user_uuid]
             {:result-set-fn doall}))

(defn get-images []
  (sql/query db
             ["SELECT * from images"] 
             {:result-set-fn doall} ))

(defn count-images []
  (sql/query db
             ["SELECT count(*) as count from images"]
             {:result-set-fn first}))

(defn get-gallery-previews []
  (sql/query db
             ["select * from 
        (select *, row_number() over (partition by user_uuid) as row_number from images)
        as rows where row_number = 1"]
             {:result-set-fn doall}))

(defn delete-image [user_uuid name]
  (sql/delete! db :images ["user_uuid=? and name=?" user_uuid name]))

(defn purchase [buyer-user-uuid image-user-uuid image-name]
  (sql/insert! db :purchases {:user_uuid buyer-user-uuid :image_user_uuid image-user-uuid :imagename image-name})
  )

(defn has-purchased [buyer-user-id image-user-id image-name]
  (sql/query db
             ["SELECT count(*) as count from purchases where user_uuid = ? and image_user_uuid = ? and imagename = ?"
              buyer-user-id image-user-id image-name] 
             {:result-set-fn first}))
