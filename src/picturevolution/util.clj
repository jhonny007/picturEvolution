(ns picturevolution.util
  (:require [noir.session :as session]
            [hiccup.util :refer [url-encode]])
  (:import java.security.MessageDigest
           java.math.BigInteger
           java.io.File))

(def thumb-prefix "thumb_")
(def galleries "galleries")

(defn gallery-path []
  (str galleries File/separator (session/get :user)) )

(defn image-uri [userid file-name]
  (str "/img/" userid "/" (url-encode file-name)))

(defn thumb-uri [userid file-name]
  (image-uri userid (str thumb-prefix file-name)))

(defn gravatar-hash
  [^String s]
  (->> s
       .trim
       .toLowerCase
       .getBytes
       (.digest (MessageDigest/getInstance "MD5"))
       (BigInteger. 1)
       (format "%032x")))
