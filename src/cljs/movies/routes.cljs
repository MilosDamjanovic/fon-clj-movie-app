(ns movies.routes
  (:require
   [bidi.bidi :as bidi]
   [pushy.core :as pushy]
   [re-frame.core :as re-frame]
   [movies.events :as events]))

(defmulti panels identity)
(defmethod panels :default [] [:div "No panel found for this route."])

(defn get-initial-panel
  "Retrieves the initial panel based weather the has the token or not"
  []
  ;; (println (events/get-user))
  (if (events/get-user)
    (re-frame/dispatch [::events/navigate [:movies-index]])
    (re-frame/dispatch[::events/navigate [:login-index]])))

(def routes
  (atom
   ["/" {""       (get-initial-panel)
         "movies" {"/all" :movies-index
                   "/create" :create-movie
                   ["/" :id "/view"] :movie-view
                   ["/" :id "/edit"] :edit-movie}
         "reviews"  {"" :movie-review-index
                           "/create" :create-movie-review
                           ["/edit/authors/" :author_id "/movies/" :movie_id ""] :edit-movie-review}
         "register" {"" :create-user
                  ["/users" ] :register-index}
         "login" :login-index
         "authors" {"" :authors-index
                    "/create" :create-author
                    ["/" :id "/edit"]  :edit-author
                    ["/" :id "/view"]  :author-view}}]))


(defn parse
  [url]
  (bidi/match-route @routes url))

(defn url-for
  [& args]
  (apply bidi/path-for (into [@routes] args)))

(defn dispatch
  [route]
  (let [panel (keyword (str (name (:handler route)) "-panel"))]
    #_(re-frame/dispatch [::events/set-active-panel panel])
    (re-frame/dispatch [::events/set-route {:route route :panel panel}])))

(defonce history
  (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (pushy/set-token! history (apply url-for handler)))

(defn start!
  []
  (pushy/start! history))

(re-frame/reg-fx
 :navigate
 (fn [handler]
   (navigate! handler)))
