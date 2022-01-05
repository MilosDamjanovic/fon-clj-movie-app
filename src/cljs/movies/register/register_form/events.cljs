(ns movies.register.register-form.events
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]))


(re-frame/reg-event-db
 ::initialise-create
 (fn [db _]
   (-> db
       (dissoc  :user-form)
       (dissoc :editing-user-id))))