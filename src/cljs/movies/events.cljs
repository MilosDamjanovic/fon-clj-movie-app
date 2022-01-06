(ns movies.events
  (:require
   [re-frame.core :as re-frame]
   [movies.db :refer [default-db set-user-ls get-user-token remove-user-ls]]
   [day8.re-frame.http-fx]
   [ajax.core :refer [json-request-format json-response-format]]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(def set-user-interceptor [(re-frame/path :user)
                           (re-frame/after set-user-ls)
                           re-frame/trim-v])


(def remove-user-interceptor [(re-frame/after remove-user-ls)])

(defn auth-header
  "Get user token and format for API authorization"
  [db]
  (when-let [token (get-in db [:user :token])]
    [:Authorization (str "Token " token)
     :Content-Type "application/json"]))

(re-frame/reg-event-fx
 ::initialise-db

 [(re-frame/inject-cofx :local-store-user)]

 (fn [{:keys [local-store-user]} _]
   {:db (assoc default-db :user local-store-user)}))


(re-frame/reg-event-fx
 ::navigate
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [_ [_ handler]]
            {:navigate handler}))

(re-frame/reg-event-fx
 ::set-active-panel
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ active-panel]]
            {:db (assoc db :active-panel active-panel)}))


(re-frame/reg-event-fx
 ::set-route
 #_{:clj-kondo/ignore [:unresolved-symbol]}
 (fn-traced [{:keys [db]} [_ route]]
            {:db (assoc db :route route)}))


(re-frame/reg-event-fx
 ::fetch-authors
 (fn [{:keys [db]} _]
   {:db   (assoc db :loading true)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:4002/api/authors"
                 :headers         (auth-header db)
                 :timeout         8000
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::fetch-authors-success]
                 :on-failure      [::bad-http-authors-result]}}))

(re-frame/reg-event-db
 ::fetch-authors-success
 (fn [db [_ data]]
   (-> db
       (assoc :loading false)
       (assoc :authors data))))

(re-frame/reg-event-fx
 ::fetch-movies
 (fn [{:keys [db]} _]
   {:db   (assoc db :loading true)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:4002/api/movies"
                 :headers         (auth-header db)
                 :timeout         8000
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::fetch-movies-success]
                 :on-failure      [::bad-http-result]}}))

(re-frame/reg-event-db
 ::fetch-movies-success
 (fn [db [_ data]]
   (-> db
       (assoc :loading false)
       (assoc :movies data))))

(re-frame/reg-event-fx
 ::fetch-genres
 (fn [{:keys [db]} _]
   {:db   (assoc db :loading true)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:4002/api/genres"
                 :headers         (auth-header db)
                 :timeout 8000
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::fetch-genres-success]
                 :on-failure      [::bad-http-result]}}))

(re-frame/reg-event-db
 ::fetch-genres-success
 (fn [db [_ data]]
   (-> db
       (assoc :loading false)
       (assoc :genres data))))

(re-frame/reg-event-fx
 ::fetch-movie-reviews
 (fn [{:keys [db]} _]
   {:db   (assoc db :loading true)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:4002/api/movie-reviews"
                 :headers         (auth-header db)
                 :timeout         8000
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::fetch-movie-reviews-success]
                 :on-failure      [::bad-http-movie-review-result]}}))

(re-frame/reg-event-db
 ::fetch-movie-reviews-success
 (fn [db [_ data]]
   (-> db
       (assoc :loading false)
       (assoc :movie-reviews data))))

(re-frame/reg-event-db
 ::bad-http-authors-result
 (fn [db [_ data]]
   (when (js/confirm "Failed to perform get-authors action on API")
     (js/console.error (str "Failed to perform get-authors action on API: ") data))))

(re-frame/reg-event-db
 ::bad-http-movie-review-result
 (fn [db [_ data]]
   (when (js/confirm "Failed to perform get-movie-reviews action on API")
     (js/console.error (str "Failed to perform get-movie-reviews action on API: ") data))))

(re-frame/reg-event-fx
 ::fetch-users
 (fn [{:keys [db]} _]
   {:db   (assoc db :loading true)
    :http-xhrio {:method          :get
                 :headers         (auth-header db)
                 :timeout         8000
                 :uri             "http://localhost:4002/api/users"
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::fetch-users-success]
                 :on-failure      [::bad-http-users-result]}}))

(re-frame/reg-event-db
 ::fetch-users-success
 (fn [db [_ data]]
   (-> db
       (assoc :loading false)
       (assoc :users data))))

(re-frame/reg-event-db
 ::bad-http-users-result
 (fn [db [_ data]]
   (when (js/confirm "Failed to perform get-users action on API")
     (js/console.error (str "Failed to perform get-users action on API: ") data))))


;; -- POST Login @ /api/login -------------------------------------------
;;
(re-frame/reg-event-fx
 :login
 (fn [{:keys [db]} [_ credentials]]
   {:db         (assoc-in db [:loading :login] true)
    :http-xhrio {:method          :post
                 :uri             "http://localhost:4002/api/login"
                 :params          credentials
                 :timeout         8000
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::login-success]
                 :on-failure      [::api-request-error {:request-type :login}]}}))

(re-frame/reg-event-fx
 ::login-success
 set-user-interceptor

 (fn [{user :db} [{props :user}]]
   {:db         (-> (merge user props))
    :dispatch-n [[::fetch-authors]
                 [::fetch-movies]
                 [::fetch-genres]
                 [::fetch-movie-reviews]
                 [::fetch-users]
                 (when (js/confirm "Successfully logged in!")
                   (re-frame/dispatch [::navigate [:movies-index]]))]}))

(defn get-user []
  (get-user-token))

(re-frame/reg-event-fx
 ::register-user
 (fn [{:keys [db]} [_]]
   {:db         (assoc-in db [:loading :register-user] true)
    :http-xhrio {:method          :post
                 :timeout         8000
                 :uri             "http://localhost:4002/api/register"
                 :params          (:user-form db)
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [::register-user-success]
                 :on-failure      [:api-request-error {:request-type :register-user}]}}))

(re-frame/reg-event-fx
 :register-user-success
 set-user-interceptor

 (fn [{user :db} [{props :user-form}]]
   {:db
    :dispatch [::navigate [:movies-index]]
    (-> (merge user props)
        :dispatch [::navigate [:movies-index]]
        (assoc-in [:loading :register-user] false))}
   (when (js/confirm "Successfully registered a new user")
     (js/console.log  (str "user: " (:username user))))))


(re-frame/reg-event-fx                                            ;; usage (dispatch [:logout])
 ::logout
 ;; This interceptor, defined above, makes sure
 ;; that we clean up localStorage after logging-out
 ;; the user.
 remove-user-interceptor
 ;; The event handler function removes the user from
 ;; app-state = :db and sets the url to "/".
 (fn [{:keys [db]} _]
   {:db       (dissoc db :user)                          ;; remove user from db
    :dispatch [::navigate [:login-index]]}))

(re-frame/reg-event-db
 ::api-request-error
 (fn [db [_ {:keys [request-type loading]} response]]
   (-> db
       (assoc-in [:errors request-type] (get-in response [:response :errors]))
       (assoc-in [:loading (or loading request-type)] false))
   (js/alert "Failed to register/login the user. Please try again later")))
