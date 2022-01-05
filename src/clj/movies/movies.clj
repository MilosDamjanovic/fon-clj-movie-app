(ns movies.movies
  (:require [movies.db :as db]
            [clojure.tools.logging :as log]))

(defn get-movies
  [_]
  {:status 200
   :body (db/get-movies db/config)})

(defn create-movie
  [{:keys [parameters]}]
  (try
    (let [data (:body parameters)
          created-id (db/insert-movie db/config data)]
      {:status 201
       :body (db/get-movie-by-id db/config created-id)})
    (catch Exception e (str (log/info e)))))

(defn get-movie-by-id
  [{:keys [parameters]}]
  (let [movie_id (:path parameters)
        movie (db/get-movie-by-id db/config movie_id)]
    (if movie
      {:status 200
       :body movie}
      {:status 404
       :body {:error "movie not found"}})))

(defn get-movies-by-author-id
  [{:keys [parameters]}]
  (try
    (let [author-id (:path parameters)
          movie (db/get-movies-by-author-id db/config author-id)]
      (if movie
        {:status 200
         :body movie}
        {:status 404
         :body {:error "movie not found"}}))
    (catch Exception e (str "-------------- Failed to get movie review: " (println e)))))

(defn update-movie
  [{:keys [parameters]}]
  (try
    (let [movie-id (get-in parameters [:path :movie-id])
          body (:body parameters)
          data (assoc body :movie-id movie-id)
          updated-count (db/update-movie-by-id db/config data)]
      (if (= 1 updated-count)
        {:status 200
         :body {:updated true
                :movie (db/get-movie-by-id db/config {:movie-id (int movie-id)})}}
        {:status 404
         :body {:updated false
                :error "Unable to update movie"}}))
    (catch Exception e (str "-------------- Failed to update movie: " (println e)))))

(defn delete-movie
  [{:keys [parameters]}]
  (let [id (:path parameters)
        before-deleted (db/get-movie-by-id db/config id)
        deleted-count (db/delete-movie-by-id db/config id)]
    (if (= 1 deleted-count)
      {:status 200
       :body {:deleted true
              :movie before-deleted}}
      {:status 404
       :body {:deleted false
              :error "Unable to delete movie"}})))
