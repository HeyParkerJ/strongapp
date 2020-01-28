(defproject strongapp "0.1.0-SNAPSHOT"
  :description "Backend for data lifting and API declaration for interfacing with the Strong app's exported data csv"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[clojure-csv/clojure-csv "2.0.1"]
                 [compojure "1.6.1"]
                 [honeysql "0.9.8"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.1.4"]
                 [seancorfield/next.jdbc "1.0.13"]
                 [ring/ring-defaults "0.3.2"]]
  :main ^:skip-aot strongapp.core
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler strongapp.handler/app}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.2"]]}})
