(ns movies.moviereview
  (:require
   [clojure.tools.logging :as log]
   [movies.db :as db]
   [movies.helper :as h]))

(defn get-movie-reviews
  [_]
  {:status 200
   :body (db/get-movie-reviews db/config)})

(defn create-movie-review [{:keys [parameters]}]
  (let [movie-review-json (:body parameters)
        aaa (update-in movie-review-json [:date-of-review] h/str-date-to-sql-date)
        xx (log/info (str "---------- create-movie-review: " aaa))
        saved (try
                (db/insert-movie-review db/config (update-in movie-review-json [:date-of-review] h/str-date-to-sql-date))
                (catch Exception e (str "----------------Movie review exception: " (println e))))]
    (log/info  (str "saved: " aaa))
    {:status  (if saved 201 400)
     :body    (when (not saved)
                "error or saving movie reivew")}))

(defn get-movie-review
  [{:keys [parameters]}]
  (let [params (:path parameters)
        movie-review (db/get-movie-review-by-author-and-movie-id db/config {:author-id (get params :author-id) :movie-id (get params :movie-id)})]
    (if movie-review
      {:status 200
       :body movie-review}
      {:status 404
       :body {:error "movie review not found"}})))


(defn update-movie-review
  [{:keys [parameters]}]
  (try
    (let [params  (:path parameters)
          body (:body parameters)
          data (assoc body :author-id (get params :author-id) :movie-id (get params :movie-id))
          request (update data :date-of-review h/str-date-to-sql-date)
          updated-count (db/update-movie-review-by-author-and-movie-id db/config request)]
      (if (= 1 updated-count)
        {:status 200
         :body {:updated true
                :movie-review (db/get-movie-review-by-author-and-movie-id db/config {:author-id (get params :author-id) :movie-id (get params :movie-id)})}}
        {:status 404
         :body {:updated false
                :error "Unable to update movie review"}}))
    (catch Exception e (str " --------------- Failed to update movie review : " e))))

(defn delete-movie-review
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
    (catch Exception e (str "Failed to delete movie review" e))))
