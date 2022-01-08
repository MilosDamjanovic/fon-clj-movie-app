(ns movies.author.authors-index.views
  (:require [re-frame.core :as re-frame]
            [movies.author.authors-index.subs :as subs]
            [movies.routes :as routes]
            [movies.events :as events]))

(defn authors-index []
  (let [authors @(re-frame/subscribe [::subs/authors])]
    [:div
     [:div.is-flex.is-align-content-center.is-justify-content-space-between.mb-6
      [:h1.is-size-3 "Authors List"]
      [:button.button.is-info.is-light
       {:on-click #(re-frame/dispatch
                    [::events/navigate [:create-author]])} "Create author"]]

     (map (fn [author] 
            [:div.mt-3
             {:key (:author_id author)}
            ;;   :on-click #(re-frame/dispatch
            ;;               [::events/navigate [:author-view :id (:author_id author)]])}
             [:span.mt-3 (str (:name author) " " (:surname author))]
             [:button.button.is-info.ml-6
              {:on-click #(re-frame/dispatch
                           [::events/navigate [:edit-author :id (:author_id author)]])} "Edit Author"][:hr]])
          (vals authors))]))

(defmethod routes/panels :authors-index-panel [] [authors-index])