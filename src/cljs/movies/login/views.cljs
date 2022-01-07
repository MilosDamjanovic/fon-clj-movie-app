(ns movies.login.views
  (:require [reagent.core :as reagent]
            [movies.routes :as routes]
            [re-frame.core :refer [dispatch]]))

(defn login-index
  []
  (let [default {:username "" :password ""}
        credentials (reagent/atom default)]
    (fn []
      (let [{:keys [username password]} @credentials
            login-user (fn [event credentials]
                         (.preventDefault event)
                         (dispatch [:login credentials]))]
          [:div.columns
           [:div.column.is-half
            [:form.box {:on-submit #(login-user % @credentials)}
             [:fieldset.field
              [:label.label {:for ""} "Username"]
              [:input.input {:type        "text"
                             :placeholder "Username"
                             :value       username
                             :on-change   #(swap! credentials assoc :username (-> % .-target .-value))
                             :required true}]]

             [:fieldset.field
              [:label.label {:for ""} "Password"]
              [:input.input {:type        "password"
                             :placeholder "Password"
                             :value       password
                             :on-change   #(swap! credentials assoc :password (-> % .-target .-value))
                                                    :required true
                             }]]
             [:button.button.is-primary.mt-4  "Log in"]]]]))))

(defmethod routes/panels :login-index-panel [] [login-index])