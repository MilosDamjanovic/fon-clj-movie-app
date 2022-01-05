(ns movies.handler
  (:require
   [movies.user :as u]
   [movies.util :refer [create-token]]))


(defn get-all-users [_]
  (try
    {:status 200
     :body (u/get-all-users)}
    (catch Exception e (str " -------------- Failed to get-all-users " e))))

(defn register
  [{:keys [parameters]}]
  (println (str "register hanlder parameters: "  parameters))
  (try
    (let [data (:body parameters)
          user (u/create-user data)]
      {:status 201
       :body {:user user
              :token (create-token user)}})
    (catch Exception e (str " ------------ Failed to register a new user: " e))))

(defn login
  [{:keys [parameters]}]
  (try
    (let [data (:body parameters)
          user (u/get-user-by-payload data)]
      (if (nil? user)
        {:status 404
         :body {:error "Invalid credentials"}}
        {:status 200
         :body {:user (assoc user :token (create-token user))}}))
    (catch Exception e (str " ----------- Failed to login:  " e))))

(defn me
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
    (catch Exception e (str " ----------- Failed to load credentials: " e))))