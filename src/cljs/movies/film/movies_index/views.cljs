(ns movies.film.movies-index.views
  (:require [re-frame.core :as re-frame]
            [movies.film.movies-index.subs :as subs]
            [movies.routes :as routes]
            [movies.events :as events]))

(defn movies-index []
  (let [movies @(re-frame/subscribe [::subs/movies])]
    [:div
     [:div.is-flex.is-align-content-center.is-justify-content-space-between.mb-6
      [:h1.is-size-3 "List of movies"]
      [:button.button.is-info.is-light.ml-3
       {:on-click #(re-frame/dispatch
                    [::events/navigate [:create-movie]])} "Create movie"]]

     (map (fn [movie] [:div {:key (:movie_id movie)
                             :on-click #(re-frame/dispatch [::events/navigate [:movie-view :id (:movie_id movie)]])}]
            [:div.mt-3
             {:key (:movie_id movie)}
             [:span.mt-5 (str (:movie_title movie) " " (:year_of_issue movie) ", " (:author_name movie) " " (:author_surname movie))]
             [:button.button.is-info.ml-6
              {:on-click #(re-frame/dispatch
                           [::events/navigate [:edit-movie :id (:movie_id movie)]])} "Edit movie"]]) movies)]))

(defmethod routes/panels :movies-index-panel [] [movies-index])