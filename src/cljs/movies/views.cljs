(ns movies.views
  (:require
   [re-frame.core :as re-frame]
   [movies.events :as events]
   [movies.routes :as routes]
   [movies.subs :as subs]))


(defn main-panel []
  (let [user @(re-frame/subscribe [:user])
        active-panel (re-frame/subscribe [::subs/active-panel])
        route-params (re-frame/subscribe [::subs/route-params])]
    [:div
     [:nav.navbar.is-dark.is-spaced
      (js/console.warn (str "user views.cljs" user))
      (if (empty? user)
        [:div.navbar-start
         [:a.navbar-item {:on-click #(re-frame/dispatch [::events/navigate [:create-user]])} "Sign up"]]
        [:div.navbar-start
         [:a.navbar-item {:on-click #(re-frame/dispatch [::events/navigate [:movies-index]])} "Movies"]
         [:a.navbar-item {:on-click #(re-frame/dispatch [::events/navigate [:authors-index]])} "Directors"] ; make more complex
         [:a.navbar-item {:on-click #(re-frame/dispatch [::events/navigate [:movie-review-index]])} "Manage movie reivews"]
         [:a.navbar-item {:on-click #(re-frame/dispatch [::events/navigate [:register-index]])} "Users"]
         [:a.navbar-item {:on-click #(re-frame/dispatch [::events/logout])} "Logout"]])]

     [:div.container.is-max-widescreen.mt-5
      [:div (routes/panels @active-panel @route-params)]]]))

