(ns datomic.schema)

(def data-model
  [{:db/ident :todo/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Task ID"}
   {:db/ident :todo/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Task Name"}
   {:db/ident :todo/completed
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc         "Task Completion Status"}
   {:db/ident :todo/updated-at
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Last Update"}])
  
