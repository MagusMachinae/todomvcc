(ns todomvcc.datomic.queries
  (:require [datomic.client.api :as datomic]
            [todomvcc.datomic.db :as db]
            [com.yetanalytics.squuid.uuid :as squuid])
  (:import (java.time Instant)))

(def all-todos '[:find (pull ?e [*])
                 :where [?e :todo/id]])

(def todo-by-id '[:find (pull ?e [*])
                  :where [?e :todo/id id]])
                    

(def active-todos '[:find (pull ?e [*])
                    :where 
                    [?e :todo/completed false]
                    [?e :todo/id]])
                    
(def completed-todos '[:find (pull ?e [*])
                       :where
                       [?e :todo/completed true]
                       [?e :todo/id]])
                       
(def todo-status '[:find ?status
                   :where [?e :todo/status]
                          [?e :todo/id id]])

(defn entity-history [id]
  @(datomic/q todo-by-id
             (datomic/history db/database) 
             id))

(defn entity-as-of [id txn-or-instant]
  @(datomic/q todo-by-id
             (datomic/as-of db/database txn-or-instant) 
             id))

(defn status-as-of [id txn-or-instant]
  @(datomic/q todo-status
             (datomic/as-of db/database txn-or-instant)
             id))

(defn list-all-todos []
  @(datomic/q all-todos db/database))

(defn list-active-todos []
  @(datomic/q active-todos db/database))

(defn list-completed-todos []
  @(datomic/q completed-todos db/database))

(defn toggle-completion [id]
  (let [current-status (datomic/q todo-status db/database id)]
    (datomic/transact db/conn {:tx-data [[:db/add [:todo/id id] :todo/completed (not current-status)]]})))

(defn edit-title [id title]
  (datomic/transact db/conn {:tx-data [[:db/add [:todo/id id] :todo/title title]]}))

(defn create-todo [title]
  (let [now (Instant/now)] 
   (datomic/transact db/conn {:tx-data [{:todo/id (:squuid (squuid/make-squuid now (random-uuid)))
                                         :todo/title title
                                         :todo/status false
                                         :todo/updated-at now}]})))

(defn delete-todo [id]
  (datomic/transact db/conn {:tx-data [[:db/retractEntity id]]}))

(defn concurrent-update []
  (pmap (partial datomic/transact db/conn) [{:tx-data [{:todo/id db/task-1-squuid
                                                        :todo/title "Define Schema (Session A)"}
                                                       {:todo/id db/task-3-squuid
                                                        :todo/title "Write Queries (Session A)"}
                                                       {:todo/id db/task-2-squuid
                                                        :todo/completed true}]}
                                            {:tx-data [{:todo/id db/task-1-squuid
                                                        :todo/title "Define Schema (Session B)"}
                                                       {:todo/id db/task-3-squuid
                                                        :todo/title "Write Queries (Session B)"}
                                                       [:db/add [:todo/id db/task-1-squuid] :todo/completed true]]}]))
