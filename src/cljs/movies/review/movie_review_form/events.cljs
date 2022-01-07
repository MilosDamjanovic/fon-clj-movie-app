(ns movies.review.movie-review-form.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [clojure.set :as set]
            [movies.events :as events]
            [day8.re-frame.http-fx]))

(re-frame/reg-event-db
 ::initialise-edit-movie-review
 (fn [db [_ author_id movie_id]]
   (let [movie-review (first (filter #(and (= (:author_id %) (int author_id)) (= (:movie_id %) (int movie_id))) (get db :movie-reviews)))]
     (-> db
         (assoc :editing-movie-review {:author_id (int author_id) :movie_id (int movie_id)})
         (assoc :movie-review-form movie-review)))))

(re-frame/reg-event-db
 ::initialise-create
 (fn [db _]
   (-> db
       (dissoc  :movie-review-form)
       (dissoc :editing-movie-review))))


(defn get-date []
  (let [date (new js/Date)
        day (-> date (.getDate))
        month (-> date  (.getMonth))
        year (-> date (.getFullYear))
        hour (-> date (.getHours))
        minute (-> date (.getMinutes))
        second (-> date (.getSeconds))]
    (str year "-" (+ month 1) "-" day "T" hour ":" minute ":" second)))

(re-frame/reg-sub
 ::get-dropdown-movies
 (fn [db _]
   (into [] (map #(set/rename-keys % {:movie_id :id :movie_title :label}) (get db :movies [])))))

(re-frame/reg-sub
 ::get-dropdown-authors
 (fn [db [_ _]]
   (into [] (map #(set/rename-keys % {:author_id :id :name :label})
                 (get db :authors [])))))


(defn construct-url [author-id movie-id]
  (str "http://localhost:4002/api/movie-reviews/authors/" author-id "/movies/" movie-id))

(def typetrans {:author_id #(js/parseInt %) :movie_id #(js/parseInt %) :rating #(js/parseInt %)})


(re-frame/reg-event-fx
 ::save-review
 (fn [{:keys [db]} [_ editing-movie-review]]
   (let [form-data (:movie-review-form db)
         method (if editing-movie-review :PUT :POST)
         review (if editing-movie-review (dissoc form-data :movie_id :author_id :movie_name :year_of_issue :author_name :author_surname form-data) (dissoc form-data :movie_name :year_of_issue :author_name :author_surname form-data))
         movie-review-date (assoc review :date_of_review (get-date))
         prepare-movie-review-date (if editing-movie-review (update-in movie-review-date [:rating] #(js/parseInt %)) (reduce-kv #(update-in %1 [%2] %3) movie-review-date typetrans))
         request (if editing-movie-review (set/rename-keys prepare-movie-review-date {:date_of_review "date-of-review"}) (set/rename-keys {:date_of_review "date-of-review" :movie_id "movie-id" :author_id "author-id"} prepare-movie-review-date))
         uri (if editing-movie-review (construct-url (get form-data :author_id) (get form-data :movie_id)) "http://localhost:4002/api/movie-reviews")]
     {:http-xhrio {:method          method
                   :uri             uri
                   :headers         (events/auth-header db)
                   :timeout         8000
                   :params          request
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::saved-movie-review]
                   :on-failure      [::api-fail]}})))

(re-frame/reg-event-db
 ::saved-movie-review
 (fn [db [_ result]]
   (js/console.warn (str "--------- saved-movie: ------------" result))
   (when (js/confirm "Successfully updated/created movie-review!")
     (-> db
                         ;; (update :movie-reviews assoc (:result db))
         )
     (re-frame/dispatch [::events/navigate [:movie-review-index]]))))


(re-frame/reg-event-fx
 ::delete-review
 (fn [{:keys [db]} [_ editing-movie-review]]
   (let [form-data (:movie-review-form db)
         uri  (construct-url (get editing-movie-review :author_id) (get editing-movie-review :movie_id))]
     {:http-xhrio {:method          :DELETE
                   :uri             uri
                   :timeout         8000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::deleted-movie-review]
                   :on-failure      [::api-fail]}})))

(re-frame/reg-event-db
 ::deleted-movie-review
 (fn [db [_ result]]
   (when (js/confirm "Successfully deleted movie review")
     (re-frame/dispatch [::events/navigate [:movie-review-index]]))
   ;(assoc db :success-http-result result)
   ))

(re-frame/reg-event-db
 ::api-fail
 (fn [db [_ result]]
   (when (js/confirm "Failed to perform delete/update action on API")
     (js/console.error (str "Failed to perform delete/update action on author: ") result))))