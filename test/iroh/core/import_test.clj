(ns iroh.core.import-test
  (:use midje.sweet)
  (:require [iroh.core.import :refer :all]))

^{:refer iroh.core.import/>var :added "0.1.10"}
(fact "imports a class method into the current namespace."

  (>var hash-without [clojure.lang.IPersistentMap without])

  (with-out-str (clojure.repl/doc hash-without))
  => (str "-------------------------\n"
          "iroh.core.import-test/hash-without\n"
          "[[clojure.lang.IPersistentMap java.lang.Object]]\n"
          "  \n"
          "member: clojure.lang.IPersistentMap/without\n"
          "type: clojure.lang.IPersistentMap\n"
          "modifiers: instance, method, public, abstract\n")

  (eval '(hash-without {:a 1 :b 2} :a))
  => {:b 2})


^{:refer iroh.core.import/>ns :added "0.1.10"}
(fact "imports all class methods into its own namespace."

  (map #(.sym %)
       (>ns test.string String :private #"serial"))
  => '[serialPersistentFields serialVersionUID])
