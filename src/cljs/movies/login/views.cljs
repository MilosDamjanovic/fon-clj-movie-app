(ns movies.login.views
  (:require [reagent.core :as reagent]
            [movies.routes :as routes]
            [movies.events :as events]
            [re-frame.core :refer [subscribe dispatch]]))

(defn login-index
  []
  (let [default {:username "" :password ""}
        credentials (reagent/atom default)]
    (fn []
      (let [{:keys [username password]} @credentials
            ;; loading @(subscribe [:loading])
            errors @(subscribe [:errors])
            login-user (fn [event credentials]
                         (.preventDefault event)
                         (dispatch [:login credentials]))]
        [:div.auth-page
         [:div.container.page
          [:div.row
           [:div.col-md-6.offset-md-3.col-xs-12
            [:h1.text-xs-center "Sign in"]
            [:p.text-xs-center
            ;;  [:a {:href (url-for :register)} "Need an account?"] refactor navigation
             ]
            ;; (when (:login errors)
            ;;   [errors-list (:login errors)]) ;; errors
            [:form {:on-submit #(login-user % @credentials)}
             [:fieldset.form-group
              [:input.form-control.form-control-lg {:type        "text"
                                                    :placeholder "Username"
                                                    :value       username
                                                    :on-change   #(swap! credentials assoc :username (-> % .-target .-value))
                                                    ;; :disabled    (:login loading)
                                                    }]]

             [:fieldset.form-group
              [:input.form-control.form-control-lg {:type        "password"
                                                    :placeholder "Password"
                                                    :value       password
                                                    :on-change   #(swap! credentials assoc :password (-> % .-target .-value))
                                                    ;; :disabled    (:login loading)
                                                    }]]
             [:button.btn.btn-lg.btn-primary.pull-xs-right  "Sign in"]]]]]]))))

(defmethod routes/panels :login-index-panel [] [login-index])