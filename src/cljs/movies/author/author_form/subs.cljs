(ns movies.author.author-form.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
 ::edit-id
 (fn [db _]
   (:editing-author-id db)))