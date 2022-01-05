(ns movies.login.subs
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::user
 (fn [db _]
   (:user db)))

(re-frame/reg-sub
 ::loading
 (fn [db]
   (:loading db)))