(ns movies.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [movies.events :as events]
   [movies.routes :as routes]
   [movies.views :as views]
   [movies.film.movies-index.views]
   [movies.login.views]
   [movies.register.register-index.views]
   [movies.register.register-form.views]
   [movies.film.movie-view.views]
   [movies.film.movie-form.views]
   [movies.author.authors-index.views]
   [movies.author.author-view.views]
   [movies.author.author-form.views]
   [movies.review.movie-review-index.views]
   [movies.review.movie-review-form.views]))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (routes/start!)
  (re-frame/dispatch-sync [::events/initialise-db])
  (mount-root))

