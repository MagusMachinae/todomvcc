(ns todomvcc.xtdb.queries
  (:require [todomvcc.xtdb.db :as db]
            [xtdb.api :as xt]))

(def all-todos 
  '(-> (from :todos [*])))

(def todo-by-id
  (concat all-todos
          '(where (= xt/id $id))))      

(def todo-status 
  '(-> (from :todos [todo/completed])
       (where (= xt/id $id))))

(def active-todos
  (concat all-todos
          '(where (= todo/completed false))))

(def completed-todos
  (concat all-todos
          '(where (= todo/completed true))))

(def todo-status-as-of
  '(-> (from :todos {:bind [todo/completed]
                     :for-valid-time (at $time)})))

(def todo-as-of 
  '(-> (from :todos {:bind [todo/completed xt/id todo/title]
                     :for-valid-time (at $time)})))

(def todo-history
  '(-> (from :todos {:bind [todo/completed xt/id todo/title]
                     :for-valid-time :all-time})))

(defn entity-history [id]
  (xt/q db/conn 
        todo-history
        {:args {:id id}}))

(defn entity-as-of [id instant]
  (xt/q db/conn
        todo-as-of
        {:args {:id id
                :time instant}}))

(defn status-as-of [id instant]
  (xt/q db/conn
        todo-status-as-of
        {:args {:id id
                :time instant}}))

(defn list-all-todos []
  (xt/q db/conn all-todos))

(defn list-active-todos []
  (xt/q db/conn active-todos))

(defn list-completed-todos []
  (xt/q db/conn completed-todos))

(defn toggle-completion [id]
  (let [current-status (xt/q db/conn todo-status {:args {:id id}})]
    (xt/execute-tx db/conn [:patch-docs :todos {:xt/id id :todo/completed (not current-status)}])))
                            
(defn insert-todo [title]
  (xt/execute-tx db/conn [:put-docs :todos {:xt/id (random-uuid) :todo/title title :todo/completed false}]))

(defn edit-title [id title]
  (xt/execute-tx db/conn [:patch-docs :todos {:xt/id id :todo/title title}]))

(defn delete-todo [id]
  (xt/execute-tx db/conn [:patch-docs :todos id]))