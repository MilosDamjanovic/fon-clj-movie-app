(ns movies.register.register-form.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::edit-id
 (fn [db _]
   (:editing-user-id db)))