(defproject todomvcc "0.1.0"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [com.xtdb/xtdb-api "2.0.0"]
                 [org.postgresql/postgresql "42.7.7"]
                 [com.github.seancorfield/next.jdbc "1.3.1048"]]
  :source-paths ["src"]
  :resource-paths ["resources"]
  :profiles {}
  :aliases {}
  :jvm-opts ["--add-opens=java.base/java.nio=ALL-UNNAMED"
             "-Dio.netty.tryReflectionSetAccessible=true"]
  :target-path "target/%s"
  :main todomvcc.shared.server)