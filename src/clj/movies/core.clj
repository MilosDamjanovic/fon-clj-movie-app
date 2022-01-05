(ns movies.core
  (:require [movies.routes :refer [ping-routes auth-routes author-routes genre-routes movie-routes movie-review-routes]]
            [muuntaja.core :as m]
            [org.httpkit.server :refer [run-server]]
            [reitit.coercion.schema]
            [reitit.ring :as ring]
            [reitit.ring.coercion :refer [coerce-exceptions-middleware
                                          coerce-request-middleware
                                          coerce-response-middleware]]
            [reitit.ring.middleware.exception :refer [exception-middleware]]
            [reitit.ring.middleware.muuntaja :refer [format-request-middleware
                                                     format-response-middleware
                                                     format-negotiate-middleware]]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defonce server (atom nil))

(def app
  (ring/ring-handler
   (ring/router
    [["/api"
      ping-routes
      auth-routes
      author-routes
      movie-review-routes
      genre-routes
      movie-routes]]
    {:data {:coercion reitit.coercion.schema/coercion
            :muuntaja m/instance
            :middleware [[wrap-cors
                          :access-control-allow-origin [#"http://localhost:4200", #"http://localhost:8280"]
                          :access-control-allow-methods [:get :post :put :delete]]
                         parameters-middleware
                         format-negotiate-middleware
                         format-response-middleware
                         exception-middleware
                         format-request-middleware
                         coerce-exceptions-middleware
                         coerce-request-middleware
                         coerce-response-middleware]}})
   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Route not found"})}))))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main []
  (println "Server started")
  (reset! server (run-server app {:port 4002})))

(defn restart-server []
  (stop-server)
  (-main))

