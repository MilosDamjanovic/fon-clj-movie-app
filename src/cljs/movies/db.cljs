(ns movies.db
  (:require
   [cljs.reader]
   [re-frame.core :refer [reg-cofx]]))

(def default-db
  {
   :movies []
   :genres []
   :authors []
   :movie-reviews []})

(def user-key "user")

(defn set-user-ls
  [user]
  (.setItem js/localStorage user-key (str user)))

(defn get-user-token []
  (.getItem js/localStorage user-key ))


(defn remove-user-ls
  []
  (.removeItem js/localStorage user-key))

(reg-cofx
 :local-store-user
 (fn [cofx _]
   (assoc cofx :local-store-user
          (into (sorted-map)
                (some->> (.getItem js/localStorage user-key)
                         (cljs.reader/read-string))))))
