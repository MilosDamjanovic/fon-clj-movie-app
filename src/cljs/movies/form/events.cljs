(ns movies.form.events
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-event-db
 ::update-form
 (fn [db [_ id val form]]
   (case form
     "movie" (assoc-in db [:movie-form id] val)
     "user" (assoc-in db [:user-form id] val)
     "movie-review" (assoc-in db [:movie-review-form id] val)
     "author" (assoc-in db [:author-form id] val))))
