(ns movies.helper
  (:require
   [clojure.string :as str]))

(defn str-date-to-sql-date [x]
  (java.sql.Timestamp/valueOf  (str/replace x "T" " ")))