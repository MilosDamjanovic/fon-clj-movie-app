(ns movies.genre
  (:require [movies.db :as db]))

(defn get-genres
  "Retrieves the movie genres from the database"
  [_]
  
  {:status 200
   :body (db/get-genres db/config)})