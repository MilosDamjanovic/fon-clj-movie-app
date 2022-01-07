(ns movies.film.movie-form.views
  (:require [movies.routes :as routes]
            [re-frame.core :as re-frame]
            [movies.film.movie-form.events :as events]
            [movies.film.movie-form.subs :as subs]
            [movies.form.views :refer [text-input select-input]]
            [movies.form.subs :as form-subs]))


(defn get-genres []
  @(re-frame/subscribe [::events/get-dropdown-genres]))

(defn get-authors []
  @(re-frame/subscribe [::events/get-dropdown-authors]))

(defn movie-form []
  (let [edit-id @(re-frame/subscribe [::subs/edit-id])
        form-valid? @(re-frame/subscribe [::form-subs/m-form-is-valid? [:movie_title :genre_id :author_id :year_of_issue]])
        movie (if edit-id "Update movie" "Create movie")]
    [:div
     [:h1.is-size-3 movie]
     [:hr]
     [:div.columns
      [:div.column.is-half
       (text-input :movie_title "Movie title" "Movie title" "movie")
       (select-input :genre_id "Select genre" (get-genres) "movie")
       (select-input :author_id "Select movie director" (get-authors) "movie")
       (text-input :year_of_issue "Year of issue" "Year of issue" "movie")

       [:div.column.is-half.is-flex-direction-column
        [:button.button.is-info.is-light.ml-3.mt-6
         {:on-click #(when (js/confirm "Are you sure?")
                       (re-frame/dispatch [::events/delete-movie edit-id]))} "Delete"]
        [:button.button.is-primary..ml-3.mt-6 {:disabled (not form-valid?)
                                         :on-click #(re-frame/dispatch [::events/save-movie edit-id])} (if edit-id "Update" "Save")]]]]]))


(defn create-movie-panel []
  (re-frame/dispatch [::events/initialise-create])
  [movie-form])

(defmethod routes/panels :create-movie-panel [] [create-movie-panel])

(defn edit-movie-panel [route-params]
  (re-frame/dispatch [::events/initialise-edit-movie (:id route-params)])
  [movie-form])

(defmethod routes/panels :edit-movie-panel [_ route-params] [edit-movie-panel route-params])



