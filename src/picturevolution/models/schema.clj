(ns picturevolution.models.schema
  (:require [picturevolution.models.db :refer :all]
            [clojure.java.jdbc :as sql]))

(defn create-users-table []
  (sql/db-do-commands
    db
    (sql/create-table-ddl
     :users
     [[:uuid "UUID PRIMARY KEY"]
      [:id "VARCHAR(80)"]
      [:pass "VARCHAR(100)"]
      [:email "VARCHAR(120)"]
      [:created "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]])
    ))

(defn drop-users-table []
  (sql/db-do-commands
    db
    (sql/drop-table-ddl
     :users)))

(defn create-images-table []
  (sql/db-do-commands
    db
    (sql/create-table-ddl
     :images
     [[:user_uuid "UUID"]
      [:name "VARCHAR(100)"]
      [:secondartist_user_uuid "UUID"]
      [:created "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]])
    ))

(defn drop-images-table []
  (sql/db-do-commands
    db
    (sql/drop-table-ddl
     :images)))

(defn create-purchases-table []
  (sql/db-do-commands
    db
    (sql/create-table-ddl
     :purchases
     [[:user_uuid "UUID"]
      [:image_user_uuid "UUID"]
      [:imagename "VARCHAR(100)"]
      [:created "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]])
    ))

(defn drop-purchases-table []
  (sql/db-do-commands
    db
    (sql/drop-table-ddl
     :purchases)))



(defn insert-users []
  (add-user-record {:uuid  "33859952-f169-460b-b90f-245f76dee512" :id "chris" :email "christian.gruber@web.de" :pass "$s0$e0801$L2d1bXLWQ1nVgifWMSf5rA==$4x06aFCKxmo/CoOlNzqQ7EslUrQbx6VPUQziCh85aP4="})
  (add-user-record {:uuid "cdf32aaa-449e-426f-8ed4-6f288abad999" :id "larissa" :pass "$s0$e0801$L2d1bXLWQ1nVgifWMSf5rA==$4x06aFCKxmo/CoOlNzqQ7EslUrQbx6VPUQziCh85aP4="})
  (add-user-record {:uuid "b41897fd-725a-4a5c-916d-5ef8b3080d91" :id "gregor" :email "gregor.n.gruber@web.de" :pass "$s0$e0801$L2d1bXLWQ1nVgifWMSf5rA==$4x06aFCKxmo/CoOlNzqQ7EslUrQbx6VPUQziCh85aP4="})
  (add-user-record {:uuid "213d43bd-37e3-4bda-8051-17a8577ef74d" :id "sophie" :pass "$s0$e0801$L2d1bXLWQ1nVgifWMSf5rA==$4x06aFCKxmo/CoOlNzqQ7EslUrQbx6VPUQziCh85aP4="})
)

(defn insert-images []
  (add-image "33859952-f169-460b-b90f-245f76dee512" "jhonny_1.jpeg" nil )
  (add-image "33859952-f169-460b-b90f-245f76dee512" "jhonny_1_1.jpeg" nil )
  (add-image "33859952-f169-460b-b90f-245f76dee512" "jhonny_1_2.jpeg" nil )
  (add-image "33859952-f169-460b-b90f-245f76dee512" "jhonny_2.jpeg" nil )
  (add-image "cdf32aaa-449e-426f-8ed4-6f288abad999" "larissa_1.jpeg" "33859952-f169-460b-b90f-245f76dee512")
  (add-image "cdf32aaa-449e-426f-8ed4-6f288abad999" "IMG_20180826_103358014.jpg" nil)
  (add-image "b41897fd-725a-4a5c-916d-5ef8b3080d91" "gregor_1.jpeg" nil)
  )

(defn insert-purchases []
  (purchase "33859952-f169-460b-b90f-245f76dee512" "b41897fd-725a-4a5c-916d-5ef8b3080d91" "gregor_1.jpeg"))

(defn drop-database []

  ;; drop
  (drop-purchases-table)
  (drop-images-table)
  (drop-users-table)
  )

(defn create-database []

  ;; create
  (create-users-table)
  (create-images-table)
  (create-purchases-table)


  ;; insert
  (insert-users)
  (insert-images)
  (insert-purchases)
  )
