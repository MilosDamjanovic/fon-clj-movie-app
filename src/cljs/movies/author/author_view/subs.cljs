
(ns movies.author.author-view.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::author
 (fn [db [_ author_id]]
   (first (filter (fn [aut] (= (:author_id aut) (int author_id))) (:authors db)))))

(re-frame/reg-sub
 ::select-author
 (fn [db [_ author_id]]
   (js/console.warn author_id)
   (->> (:authors db)
        (filter #(= (:author_id %) author_id))
        first)))