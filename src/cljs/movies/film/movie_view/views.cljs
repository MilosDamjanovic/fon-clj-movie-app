(ns movies.film.movie-view.views
  (:require [re-frame.core :as re-frame]
            [movies.routes :as routes]
            [movies.film.movie-view.subs :as subs]
            [movies.events :as events]
            [movies.subs :as route-subs]))

(defn movie-view []
  (let [route-params @(re-frame/subscribe [::route-subs/route-params])
        movie @(re-frame/subscribe [::subs/movie (:id route-params)])]
    [:div 
     (str "The selected movie is " (:movie_title movie) ", " (:year_of_issue movie) "  directed by " (:author_name movie) " " (:author_surname movie))
    ;;  [:button {:on-click #(re-frame/dispatch [::events/navigate [:create-movie]])} "Create movie"]
     ]))

(defn movie-view-panel [route-params]
  (let [selected-movie @(re-frame/subscribe [::subs/select-movie (int (:id route-params))])]
    [movie-view selected-movie]))

(defmethod routes/panels :movie-view-panel [_ route-params] [movie-view-panel route-params])