(ns movies.film.movie-view.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::movie
 (fn [db [_ movie_id]]
   (first (filter (fn [aut] (= (:movie_id aut) (int movie_id))) (:movies db)))))

(re-frame/reg-sub
 ::select-movie
 (fn [db [_ movie_id]]
   (js/console.warn movie_id)
   (->> (:movies db)
        (filter #(= (:movie_id %) movie_id))
        first)))