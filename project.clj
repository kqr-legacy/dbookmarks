(defproject bookmarks "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.7"]
            [ragtime/ragtime.lein "0.3.7"]
            [lein-environ "1.0.0"]]
  :ring {:handler bookmarks.views/app}
  :ragtime {:migrations ragtime.sql.files/migrations
            :database ~(clojure.string/trim-newline (slurp "database.cfg"))}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.namespace "0.2.7"]
                 [ring/ring "1.3.1"]
                 [com.cemerick/friend   "0.2.1"]
                 [compojure "1.1.9"]
                 [liberator "0.12.1"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [ragtime/ragtime.sql.files "0.3.7"]
                 [korma "0.3.0"]])
