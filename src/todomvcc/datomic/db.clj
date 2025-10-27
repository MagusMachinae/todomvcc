(ns todomvcc.datomic.db
  (:require [datomic.client.api :as datomic]
            [todomvcc.datomic.schema :as schema]
            [collosal.squuid.uuid :as squuid])
  (:import (java.time Instant)))

(def cfg {:server-type :datomic-local
          :system "datomic-impl"})

(def client (datomic/client cfg))

(defonce initialised-client (datomic/create-database-client client {:db-name "todos"}))


(def conn (datomic/connect client {:db-name "todos"}))

(defonce transact-schema (datomic/transact conn {:tx-data schema/data-model}))

(def database (datomic/db conn))

(defonce task-1-squuid (squuid/make-squuid (Instant/now) #uuid"b46c09c2-ed26-4d71-ac38-9517df21277d"))

(defonce task-2-squuid (squuid/make-squuid (Instant/now) #uuid"9a28ee74-c76a-4c84-b7bf-15e76c8263f7"))

(defonce task-3-squuid (squuid/make-squuid (Instant/now) #uuid"ba4e71ac-7e19-4a91-b6a9-8818212f114f"))

(defonce initial-transactions
  (datomic/transact conn {:tx-data [{:todo/id task-1-squuid
                                     :todo/title "Task 1"
                                     :todo/status false
                                     :todo/updated-at (.Instant/now)}
                                    {:todo/id task-2-squuid
                                     :todo/title "Task 2"
                                     :todo/status false
                                     :todo/updated-at (.Instant/now)}
                                    {:todo/id task-3-squuid
                                     :todo/title "Task 3"
                                     :todo/status false
                                     :todo/updated-at (.Instant/now)}
                                    [:db/add [:todo/id task-2-squuid] :todo/title "Better Names for Todos"]]}))
  
