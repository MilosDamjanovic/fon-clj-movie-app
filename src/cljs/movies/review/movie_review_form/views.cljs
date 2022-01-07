(ns movies.review.movie-review-form.views
  (:require [movies.routes :as routes]
            [re-frame.core :as re-frame]
            [movies.review.movie-review-form.events :as events]
            [movies.review.movie-review-form.subs :as subs]
            [movies.form.views :refer [ number-input text-area select-input]]
            [movies.form.subs :as form-subs]))


(defn get-movies []
  @(re-frame/subscribe [::events/get-dropdown-movies]))

(defn get-authors []
  @(re-frame/subscribe [::events/get-dropdown-authors]))

(defn movie-review-form []
  (let [edit-id @(re-frame/subscribe [::subs/edit-id])
        form-valid? @(re-frame/subscribe [::form-subs/mr-form-is-valid? [:review :rating :movie_id :author_id]])
        review (if edit-id "Update review" "Create review")]
    [:div
     [:h1.is-size-3 review]
     [:hr]
     [:div.columns
      [:div.column.is-half
       (select-input :movie_id "Select movie" (get-movies) "movie-review")
       (select-input :author_id "Select movie director" (get-authors) "movie-review")
       (text-area :review "Review" "Review" "movie-review")
       (number-input :rating "Rating" "Rating" "movie-review")
       [:button.button.is-danger.mr-3.mt-6
        {:on-click #(when (js/confirm "Are you sure?")
                      (re-frame/dispatch [::events/delete-review edit-id]))} "Delete author"]
       [:button.button.is-primary.mt-6.ml-3 {:disabled (not form-valid?)
                                             :on-click #(re-frame/dispatch [::events/save-review edit-id])} (if edit-id "Update" "Save")]][:hr]]]))


(defn create-movie-review-panel []
  (re-frame/dispatch [::events/initialise-create])
  [movie-review-form])

(defmethod routes/panels :create-movie-review-panel [] [create-movie-review-panel])

(defn edit-movie-review-panel [route-params]
  (re-frame/dispatch [::events/initialise-edit-movie-review (:author_id route-params) (:movie_id route-params)])
  [movie-review-form])

(defmethod routes/panels :edit-movie-review-panel [_ route-params] [edit-movie-review-panel route-params])



