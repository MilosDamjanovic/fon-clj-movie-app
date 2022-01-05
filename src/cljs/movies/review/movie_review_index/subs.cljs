(ns movies.review.movie-review-index.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::movie-reviews
 (fn [db _]
   (:movie-reviews db)))