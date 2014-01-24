(defproject im.chit/iroh "0.1.0-SNAPSHOT"
  :description "Wield your java object with ease"
  :url "http://www.github.com/zcaudate/iroh"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :java-source-paths ["src/java"]
  :source-paths ["src/clojure"]
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.6.0"]]
                    :plugins [[lein-midje "3.1.3"]]}})
