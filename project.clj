(defproject rs2ca "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.async "1.2.603"]
                 [org.mongodb/mongodb-driver-reactivestreams "4.0.3"]
                 [io.r2dbc/r2dbc-postgresql "0.8.2.RELEASE"]]
  :repl-options {:init-ns rs2ca.core})
