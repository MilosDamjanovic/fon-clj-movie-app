(ns movies.film.movie-form.events
  (:require [re-frame.core :as re-frame]
            [movies.events :as events]
            [ajax.core :as ajax]
            [clojure.set :as set]
            [day8.re-frame.http-fx]))


(re-frame/reg-event-db
 ::initialise-edit-movie
 (fn [db [_ movie_id]]
   (let [movie (first (filter #(= (:movie_id %) (int movie_id)) (get db :movies)))]
     (-> db
         (assoc :editing-movie-id (int movie_id))
         (assoc :movie-form movie)))))


(re-frame/reg-event-db
 ::initialise-create
 (fn [db _]
   (-> db
       (dissoc  :movie-form)
       (dissoc :editing-movie-id))))

(re-frame/reg-sub
 ::get-dropdown-genres
 (fn [db _]
   (into [] (map #(set/rename-keys % {:genre_id :id :name :label}) (get db :genres [])))))

(re-frame/reg-sub
 ::get-dropdown-authors
 (fn [db [_ _]]
   (into [] (map #(set/rename-keys % {:author_id :id :name :label})
                 (get db :authors-array [])))))


(defn construct-url [movie-id]
  (str "http://localhost:4002/api/movies/" movie-id))

(def typetrans {:author_id #(js/parseInt %) :genre_id #(js/parseInt %) :year_of_issue #(js/parseInt %)})

(re-frame/reg-event-fx
 ::save-movie
 (fn [{:keys [db]} [_ editing-movie-id]]
   (let [form-data (:movie-form db)
         movie (if editing-movie-id (dissoc form-data :name :movie_id :year-of-issue :genre-id :author_name :author_surname form-data) form-data)
         movieReq (reduce-kv #(update-in %1 [%2] %3) movie typetrans)
         request (set/rename-keys movieReq {:movie_title "movie-title" :year_of_issue "year-of-issue" :author_id "author-id" :genre_id "genre-id"})
         method (if editing-movie-id :PUT :POST)
         uri (if editing-movie-id (construct-url editing-movie-id) "http://localhost:4002/api/movies")]
     {:http-xhrio {:method          method
                   :uri             uri
                   :timeout         8000
                   :headers         (events/auth-header db)
                   :params          request
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::saved-movie]
                   :on-failure      [::api-fail]}})))

(re-frame/reg-event-db
 ::saved-movie
 (fn [db [_ result]]
   (js/console.warn (str "--------- saved-movie: ------------" result))
   (when (js/confirm "Successfully updated/created movie!")
     (-> db
                         ;; (update :movies assob (:result db))
         )
     (re-frame/dispatch [::events/navigate [:movies-index]]))))

(re-frame/reg-event-fx
 ::delete-movie
 (fn [{:keys [db]} [_ editing-movie-id]]
   (let [form-data (:movie-form db)
         uri (if editing-movie-id (construct-url editing-movie-id) "http://localhost:4002/api/movies")]
     {:http-xhrio {:method          :DELETE
                   :uri             uri
                   :headers         (events/auth-header db)
                   :timeout         8000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::deleted-movie]
                   :on-failure      [::api-fail]}})))

(re-frame/reg-event-db
 ::deleted-movie
 (fn [db [_ result]]
   (js/console.warn (str "--------- deleted-movie: ------------" result))
   (when (js/confirm "Successfully deleted movie")
     (-> db
                ;; (update :movies dissoc (:active-comment db))
         (dissoc :movie-form))
     (re-frame/dispatch [::events/navigate [:movies-index]]))))


(re-frame/reg-event-db
 ::api-fail
 (fn [db [_ result]]
   (when (js/confirm "Failed to perform delete/update action on movie: ")
     (js/console.error (str "Failed to perform delete/update action on movie: ") result))))