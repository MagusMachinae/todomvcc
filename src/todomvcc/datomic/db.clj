(ns todomvcc.datomic.db
  (:require [datomic.client.api :as datomic]
            [todomvcc.datomic.schema :as schema]
            [com.yetanalytics.squuid.uuid :as squuid])
  (:import (java.time Instant)))

(def cfg {:server-type :datomic-local
          :system "datomic-impl"
          :storage-dir :mem})

(def client (datomic/client cfg))

(defonce initialised-client (datomic/create-database client {:db-name "todos"}))

(def conn (datomic/connect client {:db-name "todos"}))

(defonce transact-schema (datomic/transact conn {:tx-data schema/data-model}))

(defonce task-1-squuid (squuid/make-squuid (Instant/now) #uuid"b46c09c2-ed26-4d71-ac38-9517df21277d"))

(defonce task-2-squuid (squuid/make-squuid (Instant/now) #uuid"9a28ee74-c76a-4c84-b7bf-15e76c8263f7"))

(defonce task-3-squuid (squuid/make-squuid (Instant/now) #uuid"ba4e71ac-7e19-4a91-b6a9-8818212f114f"))

(defonce initial-transactions
  (datomic/transact conn {:tx-data [{:todo/id (:squuid task-1-squuid)
                                     :todo/title "Task 1"
                                     :todo/completed false
                                     :todo/updated-at (java.util.Date/from (Instant/now))}
                                    {:todo/id (:squuid task-2-squuid)
                                     :todo/title "Task 2"
                                     :todo/completed false
                                     :todo/updated-at (java.util.Date/from (Instant/now))}
                                    {:todo/id (:squuid task-3-squuid)
                                     :todo/title "Task 3"
                                     :todo/completed false
                                     :todo/updated-at (java.util.Date/from (Instant/now))}]}))
                          
(defonce update-name 
  (datomic/transact conn [:db/add [:todo/id (:squuid task-2-squuid)] :todo/title "Better Names for Todos"]))

(def database (datomic/db conn))
