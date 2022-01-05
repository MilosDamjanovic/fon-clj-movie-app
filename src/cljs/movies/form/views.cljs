(ns movies.form.views
  (:require [re-frame.core :as re-frame]
            [movies.form.subs :as subs]
            [clojure.string :as str]
            [movies.form.events :as events]))

(defn field-control [id label control form-name]
  (let [value (re-frame/subscribe [::subs/form id form-name])
        update-form #(re-frame/dispatch [::events/update-form id (-> % .-target .-value) form-name])]
    [:div.field
     [:label.label label]
     [:div.control
      (control value update-form)]]))

(defn text-input [id label placeholder form-name]
  (field-control id label
                 (fn [value update-form] [:input.input {:value @value
                                                        :on-change update-form
                                                        :type "text" :placeholder placeholder}]) form-name))

(defn password-input [id label placeholder form-name]
  (field-control id label
                 (fn [value update-form] [:input.input {:value @value
                                                        :on-change update-form
                                                        :type "password" :placeholder placeholder}]) form-name))

(defn number-input [id label placeholder form-name]
  (field-control id label
                 (fn [value update-form] [:input.input {:value @value
                                                        :on-change update-form
                                                        :min "1" :max "5"
                                                        :type "number" :placeholder placeholder}]) form-name))


(defn select-input [id label options form-name]
  (field-control id label (fn [value update-form]
                            [:div.select
                             [:select {:value @value :on-change update-form}
                              [:option {:value ""} "Please select"]
                              (map (fn [o]
                                     [:option {:key (:id o) :value (:id o)} (:label o)]) options)]])form-name))


(defn text-area [id label placeholder form-name]
  (field-control id label (fn [value update-form]
                            [:textarea.textarea {:value @value
                                                 :on-change update-form
                                                 :type "text" :placeholder placeholder}]) form-name))

(defn date-picker [id label form-name]
  (field-control id label (fn [value update-form]
                            [:input {:type "datetime-local"
                                     :format "dd/MM/yyyy hh:mm:ss"
                                     :step "1"
                                     :value (first (str/split (str/replace @value " " "T") "Z"))
                                     :on-change update-form}]) form-name))
