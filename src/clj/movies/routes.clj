(ns movies.routes
  (:require
   [schema.core :as s]
   [movies.handler :as handle]
   [movies.util :refer [wrap-jwt-authentication auth-middleware]]

   [movies.movies :refer [get-movies
                          create-movie
                          get-movie-by-id
                          get-movies-by-author-id
                          update-movie
                          delete-movie]]
   [movies.genre :refer [get-genres]]
   [movies.moviereview :refer [get-movie-reviews get-movie-review create-movie-review update-movie-review delete-movie-review]]
   [movies.author :refer [get-authors create-author delete-author get-author-by-id update-author]]))

(def ping-routes
  ["/ping" {:name :ping
            :get (fn [_]
                   {:status 200
                    :body {:ping "pong"}})}])

(def movie-review-routes
  ["/movie-reviews"
   ["" {:get {:middleware [wrap-jwt-authentication auth-middleware] :handler get-movie-reviews}
        :post {:parameters
               {:body {:movie-id s/Int
                       :author-id s/Int
                       :review s/Str
                       :date-of-review s/Str
                       :rating s/Int}}
               :middleware [wrap-jwt-authentication auth-middleware] :handler create-movie-review}}]
   ["/authors/:author-id/movies/:movie-id" {:parameters {:path {:author-id s/Int :movie-id s/Int}}
                                            :get {:middleware [wrap-jwt-authentication auth-middleware] :handler get-movie-review}
                                            :put {:parameters
                                                  {:body {:review s/Str
                                                          :date-of-review s/Str
                                                          :rating s/Int}} :middleware [wrap-jwt-authentication auth-middleware] :handler update-movie-review}
                                            :delete {:middleware [wrap-jwt-authentication auth-middleware] :handler delete-movie-review}}]])

(def movie-routes
  ["/movies"
   ["" {:get {:middleware [wrap-jwt-authentication auth-middleware] :handler get-movies}
        :post {:parameters  {:body {:movie-title s/Str
                                    :genre-id s/Int
                                    :author-id s/Int
                                    :year-of-issue s/Int}}
               :middleware [wrap-jwt-authentication auth-middleware] :handler create-movie}}]
   ["/:movie-id" {:parameters {:path {:movie-id s/Int}}
                  :get get-movie-by-id
                  :put {:parameters {:body {:movie-title s/Str
                                            :genre-id s/Int
                                            :author-id s/Int
                                            :year-of-issue s/Int}}
                        :middleware [wrap-jwt-authentication auth-middleware] :handler update-movie}
                  :delete {:middleware [wrap-jwt-authentication auth-middleware] :handler delete-movie}}]
   ["/authors"
    ["/:author-id" {:parameters {:path {:author-id s/Int}}
                    :get get-movies-by-author-id}]]])

(def genre-routes
  ["/genres"
   ["" {:get {:middleware [wrap-jwt-authentication auth-middleware] :handler get-genres}}]])

(def author-routes
  ["/authors"
   ["" {:get {:middleware [wrap-jwt-authentication auth-middleware] :handler get-authors}
        :post {:middleware [wrap-jwt-authentication auth-middleware] :parameters {:body {:name s/Str
                                                                                         :surname s/Str
                                                                                         :country s/Str
                                                                                         :date-of-birth s/Str}}
               :handler create-author}}]
   ["/:author-id" {:parameters {:path {:author-id s/Int}}
                   :get {:middleware [wrap-jwt-authentication auth-middleware] :handler get-author-by-id}
                   :put {:middleware [wrap-jwt-authentication auth-middleware] :parameters {:body {:name s/Str
                                                                                                   :surname s/Str
                                                                                                   :country s/Str
                                                                                                   :date-of-birth s/Str}}
                         :handler update-author}
                   :delete {:middleware [wrap-jwt-authentication auth-middleware] :handler delete-author}}]])

;; login routes
(def auth-routes
  [["/users" {:get {:middleware [wrap-jwt-authentication auth-middleware]
                    :handler handle/get-all-users}}]
   ["/me" {:get {:middleware [wrap-jwt-authentication auth-middleware]
                 :handler handle/me}}]
   ["/register" {:post {:parameters {:body {:username s/Str
                                            :password s/Str
                                            :email s/Str}}
                        :handler handle/register}}]
   ["/login" {:post {:parameters {:body {:username s/Str
                                         :password s/Str}}
                     :handler handle/login}}]])