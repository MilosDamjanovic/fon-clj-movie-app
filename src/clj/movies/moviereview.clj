(ns movies.moviereview
  (:require
   [clojure.tools.logging :as log]
   [movies.db :as db]
   [movies.helper :as h]))

(defn get-movie-reviews
  "Retrieves all of the movie reviews from the database"
  [_]
  {:status 200
   :body (db/get-movie-reviews db/config)})

(defn create-movie-review 
  "Inserts a new movie review with complex primary key combining author and movie id and returns the newly created record"
  [{:keys [parameters]}]
  (try
    (let [movie-review-json (:body parameters)
          saved-review (db/insert-movie-review db/config (update-in movie-review-json [:date-of-review] h/str-date-to-sql-date))]
      {:status  (if saved-review 201 400)
       :body    (when-not (nil? saved-review)
                  "error or saving movie reivew"
                  (db/get-movie-review-by-author-and-movie-id db/config{:author-id (get saved-review :author_id) :movie-id (get saved-review :movie_id)})
                  )})
    (catch Exception e (log/error (str " ---------------- create-movie-review exception: " ) e))))


(defn get-movie-review
  "Retrieves single movie review"
  [{:keys [parameters]}]
  (let [params (:path parameters)
        movie-review (db/get-movie-review-by-author-and-movie-id db/config {:author-id (get params :author-id) :movie-id (get params :movie-id)})]
    (if movie-review
      {:status 200
       :body movie-review}
      {:status 404
       :body {:error "movie review not found"}})))


(defn update-movie-review
  "Update the selected by author and movie id movie review and return the updated item"
  [{:keys [parameters]}]
  (try
    (let [params  (:path parameters)
          body (:body parameters)
          data (update-in body [:date-of-review] h/str-date-to-sql-date)
          request (merge params data)
          updated-count (db/update-movie-review-by-author-and-movie-id db/config request)]
      (if (= 1 updated-count)
        {:status 200
         :body {:updated true
                :movie-review (db/get-movie-review-by-author-and-movie-id db/config {:author-id (int (get params :author-id)) :movie-id (int (get params :movie-id))})}}
        {:status 404
         :body {:updated false
                :error "Unable to update movie review"}})
      )
    (catch Exception e (log/error (str " --------------- Failed to update movie review : " ) e))))

(defn delete-movie-review
  "Delete the selected movie review and return the deleted item"
  [{:keys [parameters]}]
  (try
    (let [params (:path parameters)
          before-deleted (db/get-movie-review-by-author-and-movie-id db/config {:author-id (int (get params :author-id)) :movie-id (int (get params :movie-id))})
          deleted-count (db/delete-movie-review-by-author-and-movie-id db/config {:author-id (int (get params :author-id)) :movie-id (int (get params :movie-id))})]
      (if (= 1 deleted-count)
        {:status 200
         :body {:deleted true
                :movie-review before-deleted}}
        {:status 404
         :body {:deleted false
                :error "Unable to delete movie review"}}))
    (catch Exception e (log/error (str "Failed to delete movie review" ) e))))
