(ns movies.film.movies-index.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::movies
 (fn [db _]
   (:movies db)))