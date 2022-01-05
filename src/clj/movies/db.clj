(ns movies.db  (:require
                [hugsql.core :as hugsql]
                [clj-time [format :as timef] [coerce :as timec]]))

(def config
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//localhost:5432/clj_movies"
   :user "postgres"
   :password "postgres"})


(hugsql/def-db-fns "movies.sql")