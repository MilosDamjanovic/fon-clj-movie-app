(ns movies.form.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::form
 (fn [db [_ id form]]
   (case form
     "movie"  (get-in db [:movie-form id] "")
     "user"  (get-in db [:user-form id] "")
     "author" (get-in db [:author-form id] "")
     "movie-review" (get-in db [:movie-review-form id] ""))))

(re-frame/reg-sub
 ::form-is-valid?
 (fn [db [_ form-ids]]
   (every? #(get-in db [:author-form %]) form-ids)))

(re-frame/reg-sub
 ::u-form-is-valid?
 (fn [db [_ form-ids]]
   (every? #(get-in db [:user-form %]) form-ids)))

(re-frame/reg-sub
 ::m-form-is-valid?
 (fn [db [_ form-ids]]
   (every? #(get-in db [:movie-form %]) form-ids)))

(re-frame/reg-sub
 ::mr-form-is-valid?
 (fn [db [_ form-ids]]
   (every? #(get-in db [:movie-review-form %]) form-ids)))