(ns movies.user
  (:require
   [clojure.tools.logging :as log]
   [buddy.hashers :refer [encrypt check]]
   [movies.db :as db]))


(defn hashing [pwd]
  (str (encrypt  pwd)))

(defn create-user
  [{:keys [email username password]}]
  (try
    (let [body (hash-map :username username :email email :password password)
          request (update-in body [:password] hashing)
          created-user (db/insert-user db/config request)
          sanitized-user (dissoc created-user :password)
          msg (log/info (str "create-user " sanitized-user))]
      sanitized-user)
    (catch Exception e (str "Failed to create-user" e))))

(defn get-user-by-payload
  [{:keys [username password]}]
  (try
    (let [request (hash-map :username username)
          user (db/get-user-by-payload db/config request)
          sanitized-user (dissoc user :password)
          msg (log/info (str "get-user-by-payload " sanitized-user))]
      (if (and user (check password (:password user)))
        sanitized-user
        nil))
    (catch Exception e (str "Failed to get-user-by-payload: " e))))

(defn get-all-users []
  (let  [users  (try (db/get-users db/config) (catch Exception e (str "Failed to get-all-users" e)))
         sanitized-users (map #(dissoc % :password) users)
         msg (log/info (str "get-all-users"))]
    sanitized-users))
