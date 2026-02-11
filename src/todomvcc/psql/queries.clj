(ns todomvcc.psql.queries
  (:require [hugsql.core :as hugsql]
            [todomvcc.psql.db :as db]))

(hugsql/def-db-fns "todomvcc/psql/queries.sql")

(defn list-all-todos []
  (queries/list-todos))

(defn list-active-todos []
  (queries/list-active-todos))

(defn list-completed-todos []
  (queries/list-completed-todos))

(defn insert-todo [title]
  (queries/create-todo {:title title}))


