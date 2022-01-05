(ns movies.film.movie-form.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::edit-id
 (fn [db _]
   (:editing-movie-id db)))