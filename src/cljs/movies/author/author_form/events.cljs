(ns movies.author.author-form.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            [clojure.string :as str]
            [movies.events :as events]
            [day8.re-frame.http-fx]
            [clojure.set :as set]))


(re-frame/reg-event-db
 ::initialise-edit-author
 (fn [db [_ author_id]]
   (let [author (first (filter #(= (:author_id %) (int author_id)) (get db :authors)))]
     (-> db
         (assoc :editing-author-id (int author_id))
         (assoc :author-form author)))))


(re-frame/reg-event-db
 ::initialise-create
 (fn [db _]
   (-> db
       (dissoc :author-form)
       (dissoc :editing-author-id))))


(defn construct-url [author-id]
  "Returns the uri depending if request method is POST or PUT"
  (str "http://localhost:4002/api/authors/" author-id))

(re-frame/reg-event-fx
 ::save-author
 (fn [{:keys [db]} [_ editing-author-id]]
   (let [form-data (:author-form db)
         author (if editing-author-id (dissoc form-data :author_id form-data) form-data)
         request (set/rename-keys author {:name "name" :surname "surname" :date_of_birth "date-of-birth" :country "country"})
         method (if editing-author-id :PUT :POST)
         uri (if editing-author-id (construct-url editing-author-id) "http://localhost:4002/api/authors")]
     {:http-xhrio {:method          method
                   :uri             uri
                   :headers         (events/auth-header db)
                   :timeout         8000
                   :params          request
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::saved-author]
                   :on-failure      [:api-fail]}})))

(re-frame/reg-event-db
 ::saved-author
 (fn [db [_ result]]
   (re-frame/dispatch [::events/navigate [:authors-index]])
   (assoc db :success-http-result result)))

(re-frame/reg-event-fx
 ::delete-author
 (fn [{:keys [db]} [_ editing-author-id]]
   (let [uri (str "http://localhost:4002/api/authors/" editing-author-id)]
     {:http-xhrio {:method          :delete
                   :uri             uri
                   :timeout         8000
                   :headers         (events/auth-header db)
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:deleted-author]
                   :on-failure      [:api-fail]}})))

(re-frame/reg-event-db
 ::deleted-author
 (fn [db [_ result]]
   (when (js/confirm "Successfully deleted author")
     (re-frame/dispatch [::events/navigate [:authors-index]]))
   (assoc db :success-http-result result))
 )


(re-frame/reg-event-db
 ::api-fail
 
 (fn [db [_ result]]
   (when (js/confirm "Failed to perform delete/update action on author")
     (js/console.error (str "Failed to perform delete/update action on author: ") result))))