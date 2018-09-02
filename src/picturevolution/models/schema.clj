(ns picturevolution.models.schema
  (:require [picturevolution.models.db :refer :all]
            [clojure.java.jdbc :as sql]))

(defn create-users-table []
  (sql/with-connection
    db
    (sql/create-table
     :users
     [:id "VARCHAR(80) PRIMARY KEY"]
     [:email "VARCHAR(120)"]
     [:created "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
     [:pass "VARCHAR(100)"])
    ))

(defn drop-users-table []
  (sql/with-connection
    db
    (sql/drop-table
     :users)))

(defn create-images-table []
  (sql/with-connection
    db
    (sql/create-table
     :images
     [:userid "VARCHAR(80)"]
     [:created "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
     [:name "VARCHAR(100)"]
     [:secondartist "VARCHAR(80)"])
    ))

(defn drop-images-table []
  (sql/with-connection
    db
    (sql/drop-table
     :images)))

(defn insert-users []
  (add-user-record {:id "chris" :email "christian.gruber@web.de" :pass "$s0$e0801$L2d1bXLWQ1nVgifWMSf5rA==$4x06aFCKxmo/CoOlNzqQ7EslUrQbx6VPUQziCh85aP4="})
  (add-user-record {:id "larissa" :pass "$s0$e0801$L2d1bXLWQ1nVgifWMSf5rA==$4x06aFCKxmo/CoOlNzqQ7EslUrQbx6VPUQziCh85aP4="})
  (add-user-record {:id "gregor" :email "gregor.n.gruber@web.de" :pass "$s0$e0801$L2d1bXLWQ1nVgifWMSf5rA==$4x06aFCKxmo/CoOlNzqQ7EslUrQbx6VPUQziCh85aP4="})
  (add-user-record {:id "sophie" :pass "$s0$e0801$L2d1bXLWQ1nVgifWMSf5rA==$4x06aFCKxmo/CoOlNzqQ7EslUrQbx6VPUQziCh85aP4="})
)

(defn insert-images []
  (add-image "chris" "jhonny_1.jpeg" nil )
  (add-image "chris" "jhonny_1_1.jpeg" nil )
  (add-image "chris" "jhonny_1_2.jpeg" nil )
  (add-image "chris" "jhonny_2.jpeg" nil )
  (add-image "larissa" "larissa_1.jpeg" "chris")
  (add-image "gregor" "gregor_1.jpeg" nil)
  )

(defn recreate-database []
  ;; drop
  (drop-images-table)
  (drop-users-table)
  ;; create
  (create-users-table)
  (create-images-table)

  ;; insert
  (insert-users)
  (insert-images)
  )
