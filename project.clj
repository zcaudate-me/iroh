(defproject im.chit/iroh "0.1.9"
  :description "simple class reflection"
  :url "http://www.github.com/zcaudate/iroh"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :java-source-paths ["src/java"]
  :source-paths ["src/clojure"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.tcrawley/dynapath "0.2.3"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                    :plugins [[lein-midje "3.1.3"]]}}
  :documentation {:files {"docs/index"
                         {:input "test/midje_doc/iroh_guide.clj"
                          :title "iroh"
                          :sub-title "shining light into your classes"
                          :author "Chris Zheng"
                          :email  "z@caudate.me"}}})
