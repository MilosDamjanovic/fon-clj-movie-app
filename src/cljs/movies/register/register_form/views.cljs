(ns movies.register.register-form.views
  (:require [movies.routes :as routes]
            [re-frame.core :as re-frame]
            [movies.register.register-form.events :as events]
            [movies.events :as gevents]
            [movies.register.register-form.subs :as subs]
            [movies.form.views :refer [text-input password-input]]
            [movies.form.subs :as form-subs]))

(defn register-form []
  (let [edit-id @(re-frame/subscribe [::subs/edit-id])
        form-valid? @(re-frame/subscribe [::form-subs/u-form-is-valid? [:email :username :password]])
        user (if edit-id "Update user" "Create user")]
    [:div
     [:h1.is-size-3 user]
     [:hr]
     [:div.columns
      [:div.column.is-half
       (text-input :email "Email" "email" "user")
       (text-input :username "Username" "Please enter your username" "user")
       (password-input :password "Password" "*********" "user")
       [:button.button.is-primary.mt-4 {:disabled (not form-valid?)
                                        :on-click #(re-frame/dispatch [::gevents/register-user edit-id])} (if edit-id "Update" "Save")]]]]))


(defn create-user-panel []
  (re-frame/dispatch [::events/initialise-create])
  [register-form])

(defmethod routes/panels :create-user-panel [] [create-user-panel])