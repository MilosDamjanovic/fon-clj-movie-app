(ns movies.db  (:require
                [hugsql.core :as hugsql]
                [clj-time [format :as timef] [coerce :as timec]]))

(def config
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/clj_movies"
   :user "postgres"
   :password "postgres"})


(hugsql/def-db-fns "movies.sql")

(comment
  (create-movie-review-table config)
  (insert-movie-review config
                       {:movie-id 2
                        :author-id 2
                        :rating 4
                        :review "This is an awesome review for the id:2 movie and author id 2"
                        :date-of-review (->> "Thu Feb 09 10:38:01 +0000 1998"
                                             (timef/parse (timef/formatter "EEE MMM dd HH:mm:ss Z yyyy"))
                                             timec/to-timestamp)})
  (get-movie-review-by-author-and-movie-id config {:author-id 1 :movie-id 2})
  (update-movie-review-by-author-and-movie-id config {:movie-id 2
                                                      :author-id 2
                                                      :rating 5
                                                      :review "This is an awesome UPDATED!!! review for the id:2 movie and author id 2"
                                                      :date-of-review (->> "Thu Feb 09 10:38:01 +0000 1998"
                                                                           (timef/parse (timef/formatter "EEE MMM dd HH:mm:ss Z yyyy"))
                                                                           timec/to-timestamp)})
  (delete-movie-review-by-author-and-movie-id config {:author-id 2 :movie-id 2})
  (get-movie-reviews config)
  (get-movies config)
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (create-author-table config)
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (create-user-table config)
  (get-movie-by-id config {:movie-id 2})
  (get-author-by-id config {:author-id 1})
  (get-movies-by-author-id config {:author-id 1})
  (create-genre-table config)
  (create-movie-table config)
  (insert-movie config
                {:movie-title "The Irishman"
                 :genre-id 1
                 :author-id 1
                 :year-of-issue 2019})
  (insert-movie config {:movie-title "Spiderman"
                        :genre-id 2
                        :author-id 3
                        :year-of-issue 2021})
  (insert-genre config
                {:genre-id 1
                 :name "Crime"})
  (insert-genre config
                {:genre-id 2
                 :name "Drama"})
  (insert-genre config
                {:genre-id 3
                 :name "Commedy"})
  (insert-genre config
                {:genre-id 4
                 :name "Science fiction"})
  (insert-genre config
                {:genre-id 5
                 :name "Romance"})
  (insert-user config
               {:username "aa"
                :email "aa@username.com"
                :password "password"})
  (get-users config)
  (get-user-by-credentials config {:username "username" :password "password"})
  (get-user-by-payload config {:username "username4"})
  (insert-author config {:name "Dragan"
                         :surname "Bjelogrlic"
                         :date-of-birth (->> "Thu Feb 09 10:38:01 +0000 2012"
                                             (timef/parse (timef/formatter "EEE MMM dd HH:mm:ss Z yyyy"))
                                             timec/to-timestamp)
                         :country "Gabon"})
  (insert-author config
                 {:name "Christopher"
                  :surname "Nolan"
                  :date-of-birth  (->> "Thu Feb 19 10:38:01 +0000 2019"
                                       (timef/parse (timef/formatter "EEE MMM dd HH:mm:ss Z yyyy"))
                                       timec/to-timestamp)
                  :country "GBR"})
  (insert-author config
                 {:name "Alfred"
                  :surname "Hitchcock"
                  :date-of-birth (java.sql.Timestamp/valueOf "1899-08-08 10:23:54")
                  :country "GBR"})
  (insert-author config
                 {:name "Stanley"
                  :surname "Kubrick"
                  :date-of-birth  (java.sql.Timestamp/valueOf "2004-10-19 10:23:54")
                  :country "USA"})
  (insert-author config
                 {:name "John"
                  :surname "Wats"
                  :date-of-birth  (java.sql.Timestamp/valueOf "1975-10-19 10:23:54")
                  :country "USA"})

  (update-author-by-id config {:name "bez X"
                               :surname "Nolan"
                               :date-of-birth (timef/parse (timef/formatter "YYYY-MM-dd" "2004-10-19T10:23:54Z"))
                               :country "Serbia"})

  (update-movie-by-id config {:movie-id 2
                              :movie-title "Spiderman"
                              :genre-id 2
                              :author-id 3
                              :year-of-issue 2021}))