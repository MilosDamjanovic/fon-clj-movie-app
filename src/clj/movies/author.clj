(ns movies.author
  (:require [movies.db :as db]
            [movies.helper :as h]
            [clojure.tools.logging :as log]))


(defn get-authors
  "Retrieves the movie directors from the database"
  [_]
  {:status 200
   :body (db/get-authors db/config)})


(defn create-author
  "Inserts a new author into the database"
  [{:keys [parameters]}]
  (try
    (let [author-json (:body parameters)
          inserted-author (db/insert-author db/config (update-in author-json [:date-of-birth] h/str-date-to-sql-date))]

      {:status  (if inserted-author 201 400)
       :body    (when-not (nil? inserted-author)
                  "error on saving author"
                  (db/get-author-by-id db/config {:author-id (get inserted-author :author_id)}))})
    (catch Exception e (log/error (str "------ Failed to insert-author : ")) e )))

(defn get-author-by-id
  "Fetch single author from the database"
  [{:keys [parameters]}]
  (let [author-id (:path parameters)
        author (db/get-author-by-id db/config author-id)]
    (if author
      {:status 200
       :body author}
      {:status 404
       :body {:error "author not found"}})))

(defn update-author
  "Update author record by the id inside the database"
  [{:keys [parameters]}]
  (try
    (let [author-id (get-in parameters [:path :author-id])
          body (:body parameters)
          data (assoc body :author-id author-id)
          req (update data :date-of-birth h/str-date-to-sql-date)
          updated-count (db/update-author-by-id db/config req)]

      (if (= 1 updated-count)
        {:status 200
         :body {:updated true
                :author (db/get-author-by-id db/config {:author-id (int author-id)})}}
        {:status 404
         :body {:updated false
                :error "Unable to update author"}}))
    (catch Exception e (log/error (str " ------------ Failed to update author: "))  e)))

(defn delete-author
  "Delete single author entry in the database by author_id"
  [{:keys [parameters]}]
  (try
    (let [id (:path parameters)
          before-deleted (db/get-author-by-id db/config id)
          deleted-count (db/delete-author-by-id db/config id)]
      (if (= 1 deleted-count)
        {:status 200
         :body {:deleted true
                :author before-deleted}}
        {:status 404
         :body {:deleted false
                :error "Unable to delete author"}}))
    (catch Exception e (str " -------------------- Failed to delete author ") e)))