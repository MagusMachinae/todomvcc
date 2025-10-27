(ns datomic.queries
  (:require [datomic.client.api :as datomic]
            [todomvcc.datomic.db :as db]
            ))

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

(defn assert-new-todo [title]
  (datomic/transact db/conn {:tx-data [{:todo/title title
                                        :todo/status false
                                        :todo/updated-at (.Instant/now)}]}))

(defn retract-todo [id]
  (datomic/transact db/conn {:tx-data [[:db/retractEntity id]]}))

(defn concurrent-update []
  (pmap (partial datomic/transact db/conn) [{:tx-data [{:todo/id #uuid"b46c09c2-ed26-4d71-ac38-9517df21277d"
                                                        :todo/title "Define Schema (Session A)"}
                                                       {:todo/id #uuid"ba4e71ac-7e19-4a91-b6a9-8818212f114f"
                                                        :todo/title "Write Queries (Session A)"}
                                                       {:todo/id #uuid"9a28ee74-c76a-4c84-b7bf-15e76c8263f7"
                                                        :todo/completed true}]}
                                            {:tx-data [{:todo/id #uuid"b46c09c2-ed26-4d71-ac38-9517df21277d"
                                                        :todo/title "Define Schema (Session B)"}
                                                       {:todo/id #uuid"ba4e71ac-7e19-4a91-b6a9-8818212f114f"
                                                        :todo/title "Write Queries (Session B)"}
                                                       [:db/add [:todo/id #uuid"b46c09c2-ed26-4d71-ac38-9517df21277d"] :todo/completed true]]}]))
