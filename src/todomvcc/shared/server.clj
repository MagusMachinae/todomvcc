(ns todomvcc.shared.server
  (:require [clojure.java.io :as io]))

(def config {:port 3000})

(defn route-builder [dir]
  (let [ns-root (str "todomvcc." dir ".queries")
        list-all-todos (requiring-resolve (symbol ns-root "list-all-todos"))
        list-active-todos (requiring-resolve (symbol ns-root "list-active-todos"))
        list-completed-todos (requiring-resolve (symbol ns-root "list-completed-todos"))
        create-todo (requiring-resolve (symbol ns-root "create-todo"))
        toggle-completion (requiring-resolve (symbol ns-root "toggle-completion"))
        edit-title (requiring-resolve (symbol ns-root "edit-title"))
        delete-todo (requiring-resolve (symbol ns-root "delete-todo"))
        entity-history (requiring-resolve (symbol ns-root "entity-history"))
        entity-as-of (requiring-resolve (symbol ns-root "entity-as-of"))
        status-as-of (requiring-resolve (symbol ns-root "status-as-of"))
        history-as-of (requiring-resolve (symbol ns-root "history-as-of"))
        concurrent-update (requiring-resolve (symbol ns-root "concurrent-update"))]
    [(str dir "/")
     ["list-all-todos/"
      {:get {:handler (fn [req]
                        {:status 200
                         :body (list-all-todos)})}}]
     ["list-active-todos/"
      {:get {:handler (fn [req]
                        {:status 200
                         :body (list-active-todos)})}}]
     ["list-completed-todos/"
      {:get {:handler (fn [req]
                        {:status 200
                         :body (list-completed-todos)})}}]
     ["create-todo/"
      {:post {:handler (fn [{{{:keys [title]} :body} :parameters}]
                        {:status 200
                         :body (create-todo title)})}}]
     ["toggle-completion/"
      {:post {:handler (fn [{{{:keys [id]} :body} :parameters}]
                         {:status 200
                          :body (toggle-completion id)})}}]
     ["edit-title/"
      {:post {:handler (fn [{{{:keys [id title]} :body} :parameters}]
                         {:status 200
                          :body (edit-title id title)})}}]
     ["entity-history/"
      {:get {:handler (fn [{{{:keys [id]} :body} :parameters}]
                        {:status 200
                         :body (entity-history id)})}}]
     ["entity-as-of/"
      {:get {:handler (fn [{{{:keys [id as-of]} :body} :parameters}]
                        {:status 200
                         :body (entity-as-of id as-of)})}}]
     ["status-as-of/"
      {:get {:handler (fn [{{{:keys [id as-of]} :body} :parameters}]
                        {:status 200
                         :body (status-as-of id as-of)})}}]
     ["history-as-of/"
      {:get {:handler (fn [{{{:keys [id as-of]} :body} :parameters}]
                        {:status 200
                         :body (history-as-of id as-of)})}}]
     ["delete-todo/"
      {:delete {:handler (fn [{{{:keys [id]} :body} :parameters}]
                          {:status 200
                           :body (delete-todo id)})}}]
     ["concurrent-update/"
      {:post {:handler (fn [req]
                         {:status 200
                          :body (concurrent-update)})}}]]))

(def routes
  (let [db-impls (filter (fn [dir] (not (#{"shared"} dir))) (seq (.list (io/file "src/todomvcc"))))]
    (into ["/"] (map route-builder db-impls))))

(defn -main [])