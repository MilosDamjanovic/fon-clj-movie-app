(ns movies.handler
  (:require
   [movies.user :as u]
   [clojure.tools.logging :as log]
   [movies.util :refer [create-token]]))


(defn get-all-users
  "Returns all registered users from the database"
  [_]
  (try
    {:status 200
     :body (u/get-all-users)}
    (catch Exception e (log/error (str " -------------- Failed to get-all-users " e)))))

(defn register
  "Inserts a new user to the database and returns the newly registered user to the FE application with the signed token"
  [{:keys [parameters]}]
  (try
    (let [data (:body parameters)
          user (u/create-user data)]
      {:status 201
       :body {:user user
              :token (create-token user)}})
    (catch Exception e (log/error (str " ------------ Failed to register a new user: " e)))))

(defn login
  "Queries the user by the username and checks with the provided token whether the token is valid so that it can authenticate the user."
  [{:keys [parameters]}]
  (try
    (let [data (:body parameters)
          user (u/get-user-by-payload data)]
      (if (nil? user)
        {:status 404
         :body {:error "Invalid credentials"}}
        {:status 200
         :body {:user (assoc user :token (create-token user))}}))
    (catch Exception e (log/error (str " ----------- Failed to login:  " e)))))

(defn me
  "Verification function for testing purposes"
  [request]
  (try
    (let [payload (:identity request)
          user (u/get-user-by-payload payload)]
      (if (nil? user)
        {:status 404
         :body {:error "Invalid credentials"}}
        {:status 200
         :body {:user user
                :token (create-token user)}}))
    (catch Exception e (log/error (str " ----------- Failed to load credentials: " e)))))