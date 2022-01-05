(ns movies.author.author-view.views
  (:require [re-frame.core :as re-frame]
            [movies.routes :as routes]
            [movies.author.author-view.subs :as subs]
            [movies.subs :as route-subs]))

(defn author-view [{:keys [name surname date_of_birth country]}]
  ;; (let [route-params @(re-frame/subscribe [::route-subs/route-params])
  ;;       author @(re-frame/subscribe [::subs/author (:author_id route-params)])]
    [:div
     [:h1.title (str name " " surname)]
     [:h2.subtitle.mb-1 (str "from: " country)]
     [:h3.subtitle.is-6.mt-5 date_of_birth]
     [:h3.content.mt-5 surname]]
    ;; )
  ;; perhaps edit here
  )


(defn author-view-panel [route-params]
  (js/console.warn (str "author view " route-params)) 
  (let [selected-author @(re-frame/subscribe [::subs/select-author (int (:id route-params))])]
    [author-view selected-author]))

(defmethod routes/panels :author-view-panel [_ route-params] [author-view-panel route-params])