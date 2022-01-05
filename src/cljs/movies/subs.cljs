(ns movies.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (get-in db [:route :panel])))


(re-frame/reg-sub
 ::route-params
 (fn [db _]
   (get-in db [:route :route :route-params])))

(re-frame/reg-sub
 :errors ;; usage: (subscribe [:errors])
 (fn [db _]
   (:errors db)))

(re-frame/reg-sub
 :user ;; usage: (subscribe [:user])
 (fn [db _]
   (:user db)))
