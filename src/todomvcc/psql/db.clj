(ns todomvcc.psql.db
  (:require [next.jdbc :as jdbc]
            [hugsql.core :as hugsql]))

(def cfg {:dbtype "psql" :db-name "postgres-bitemporal"})

(def ds (jdbc/get-datasource cfg))

(hugsql/def-db-fns "todomvcc/psql/schema.sql")

(defonce initialised-db 
  (jdbc/with-transaction [tx ds]
    (jdbc/execute! tx (schema/create-todos-table))
    (jdbc/execute! tx (schema/create-history-table))
    (jdbc/execute! tx (schema/create-audit-table-callback))
    (jdbc/execute! tx (schema/create-audit-table-trigger))))
    
