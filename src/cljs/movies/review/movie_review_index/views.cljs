(ns movies.review.movie-review-index.views
  (:require [re-frame.core :as re-frame]
            [movies.review.movie-review-index.subs :as subs]
            [movies.routes :as routes]
            [movies.events :as events]))

(defn movie-review-index []
  (let [reviews @(re-frame/subscribe [::subs/movie-reviews])]
    [:div
     [:div.is-flex.is-justify-content-space-between.mb-6
      [:h1.is-size-3 "List of reviews"]
      [:button.button.is-info.is-light
       {:on-click #(re-frame/dispatch
                    [::events/navigate [:create-movie-review]])} "Create movie review"]]

     (map (fn [movie-review]
            [:div.mt-3
             {:key [(:movie_id movie-review) (:author_id movie-review)]}
             [:span.mt-5 (str (:movie_name movie-review) ": " (:year_of_issue movie-review) ", " (:author_name movie-review) " " (:author_surname movie-review))]
             [:button.button.is-info.ml-6
              {:on-click #(re-frame/dispatch
                           [::events/navigate [:edit-movie-review :author_id (:author_id movie-review) :movie_id (:movie_id movie-review)]])} "Edit movie review"]]) reviews)]))

(defmethod routes/panels :movie-review-index-panel [] [movie-review-index])