(ns todomvcc.xtdb.db
  (:require [xtdb.api :as xt-api]
            [xtdb.node :as db-node]))

(defonce conn (db-node/start-node))
