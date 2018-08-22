(defproject arena "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.combinatorics "0.1.4"]
                 [org.clojure/data.json "0.2.6"]
                 [compojure "1.5.1"]
                 [clj-http "3.9.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.2.1"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler arena.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
