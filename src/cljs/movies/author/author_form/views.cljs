(ns movies.author.author-form.views
  #_{:clj-kondo/ignore [:unresolved-var]}
  (:require [movies.routes :as routes]
            [re-frame.core :as re-frame]
            [movies.author.author-form.events :as events]
            [movies.author.author-form.subs :as subs]
            [movies.form.views :refer [text-input date-picker text-area  select-input]]
            [movies.form.subs :as form-subs]))


;; (defn image-display []
;;   (when-let [image-url @(re-frame/subscribe [::form-subs/form :image-url])]
;;     [:img {:src image-url}]))


;; (defn tag-list []
;;   (let [tags @(re-frame/subscribe [::subs/tag-list])]
;;     [:div.mt-2
;;      (map (fn [tag]
;;             [:span.tag.is-success.mr-2 {:key tag} tag
;;              [:button.delete.is-small {:on-click #(re-frame/dispatch [::events/remove-author-tag tag])}]]) tags)]))

;; (defn add-tag-input []
;;   [:div
;;    [:label "Tags"]
;;    [:div.columns
;;     [:div.column.is-one-third
;;      (text-input :tag "")]
;;     [:div.column.is-narrow.is-align-self-center.is-clickable
;;      {:on-click #(re-frame/dispatch [::events/save-author-tag])}
;;      "➕"]]])

(defn author-form []
  (let [edit-id @(re-frame/subscribe [::subs/edit-id])
        form-valid? @(re-frame/subscribe [::form-subs/form-is-valid? [:name :surname :date_of_birth :country]])
        name (if edit-id "Update author" "Create author")]
    [:div
     [:h1.name name]
     [:hr]
     [:div.columns
      [:div.column.is-half
       (text-input :name "Director's name" "Please enter director's name" "author")
       (text-input :surname "Director's surname" "Please enter director's surname" "author")
       (text-input :country "Country" "Enter country" "author")
       (date-picker :date_of_birth "Date of birth" "author")

       [:button.button.is-info.is-light.mr-3.mt-6
        {:on-click #(when (js/confirm "Are you sure?")
                      (re-frame/dispatch  [::events/delete-author edit-id]))} "Delete author"]
       [:button.button.is-primary.ml-3.mt-6 {:disabled (not form-valid?)
                                        :on-click #(re-frame/dispatch [::events/save-author edit-id])} (if edit-id "Update" "Save")]]]]))

(defn create-author-panel []
  (re-frame/dispatch [::events/initialise-create])
  [author-form])

(defmethod routes/panels :create-author-panel [] [create-author-panel])


(defn edit-author-panel [route-params]
  (re-frame/dispatch [::events/initialise-edit-author (:id route-params)])
  [author-form])


(defmethod routes/panels :edit-author-panel [_ route-params] [edit-author-panel route-params])