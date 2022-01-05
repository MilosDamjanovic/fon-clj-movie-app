(ns movies.register.register-index.views
  (:require [re-frame.core :as re-frame]
            [movies.register.register-index.subs :as subs]
            [movies.routes :as routes]
            [movies.events :as events]))

(defn register-index []
  (let [users @(re-frame/subscribe [::subs/users])]
    [:div
     [:div.is-flex.is-align-content-center.is-justify-content-space-between.mb-6
      [:h1.is-size-3 "List of registered users"]]

     (map (fn [user] 
            [:div.mt-3
             {:key (:id user)}
             [:span.mt-5 (str (:email user) " " (:username user))]]) users)]))

(defmethod routes/panels :register-index-panel [] [register-index])