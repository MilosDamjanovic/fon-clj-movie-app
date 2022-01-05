(ns movies.author.authors-index.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::authors
 (fn [db _]
   (:authors db)))

(re-frame/reg-sub
 ::loading
 (fn [db]
   (:loading db)))

