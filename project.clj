(defproject todomvcc "0.1.0"
  :dependencies [[org.clojure/clojure "1.12.3"]
                 [com.xtdb/xtdb-api "2.0.0" :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [com.xtdb/xtdb-core "2.0.0"]
                 [metosin/malli "0.16.4"]
                 [com.github.seancorfield/next.jdbc "1.3.1048"]]
  :repositories [["sonatype-snapshots" {:url "https://s01.oss.sonatype.org/content/repositories/snapshots"}]]
  :source-paths ["src"]
  :resource-paths ["resources"]
  :aliases {"xtdb" ["with-profile" "+xtdb" "run"]}
  :profiles {:xtdb 
             {:jvm-opts ["--add-opens=java.base/java.nio=ALL-UNNAMED"
                         "-Dio.netty.tryReflectionSetAccessible=true"]}}
  :target-path "target/%s"
  :main todomvcc.shared.server)
