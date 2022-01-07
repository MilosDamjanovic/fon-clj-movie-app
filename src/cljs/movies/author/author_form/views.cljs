(ns movies.author.author-form.views
  (:require [movies.routes :as routes]
            [re-frame.core :as re-frame]
            [movies.author.author-form.events :as events]
            [movies.author.author-form.subs :as subs]
            [movies.form.views :refer [text-input date-picker]]
            [movies.form.subs :as form-subs]))


(defn author-form []
  (let [edit-id @(re-frame/subscribe [::subs/edit-id])
        form-valid? @(re-frame/subscribe [::form-subs/form-is-valid? [:name :surname :date_of_birth :country]])
        name (if edit-id "Update author" "Create author")]
    [:div
     [:h1.is-size-3 name]
     [:hr]
     [:div.columns
      [:div.column.is-half
       (text-input :name "Director's name" "Please enter director's name" "author")
       (text-input :surname "Director's surname" "Please enter director's surname" "author")
       (text-input :country "Country" "Enter country" "author")
       (date-picker :date_of_birth "Date of birth" "author")

       [:div.column.is-half.is-flex-direction-column
        [:button.button.is-info.is-light.mr-3.mt-6
         {:on-click #(when (js/confirm "Are you sure?")
                       (re-frame/dispatch  [::events/delete-author edit-id]))} "Delete author"]
        [:button.button.is-primary.ml-3.mt-6 {:disabled (not form-valid?)
                                              :on-click #(re-frame/dispatch [::events/save-author edit-id])} (if edit-id "Update" "Save")]]]]]))

(defn create-author-panel []
  (re-frame/dispatch [::events/initialise-create])
  [author-form])

(defmethod routes/panels :create-author-panel [] [create-author-panel])


(defn edit-author-panel [route-params]
  (re-frame/dispatch [::events/initialise-edit-author (:id route-params)])
  [author-form])


(defmethod routes/panels :edit-author-panel [_ route-params] [edit-author-panel route-params])