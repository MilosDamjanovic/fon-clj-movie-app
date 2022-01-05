(ns movies.helper
  (:require
   [clojure.string :as str]))

(defn str-date-to-sql-date [x]
  (println  (str/replace x "T" " ") \Z)
  (java.sql.Timestamp/valueOf  (str/replace x "T" " ")))