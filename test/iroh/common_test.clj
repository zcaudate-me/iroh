(ns iroh.common-test
  (:use midje.sweet)
  (:require [iroh.common :refer :all]))

^{:refer iroh.common/suppress :added "0.1.10"}
(fact "suppress")

^{:refer iroh.common/hash-map? :added "0.1.10"}
(fact "hash-map?")

^{:refer iroh.common/regex? :added "0.1.10"}
(fact "regex?")

^{:refer iroh.common/class-array :added "0.1.10"}
(fact "class-array")

^{:refer iroh.common/assoc-if :added "0.1.10"}
(fact "assoc-if")

^{:refer iroh.common/update-in-if :added "0.1.10"}
(fact "update-in-if")

^{:refer iroh.common/select-keys-nnil :added "0.1.10"}
(fact "select-keys-nnil")

^{:refer iroh.common/is-selected-key :added "0.1.10"}
(fact "is-selected-key")

^{:refer iroh.common/select-keys-fn :added "0.1.10"}
(fact "select-keys-fn")

^{:refer iroh.common/select-keys-nested :added "0.1.10"}
(fact "select-keys-nested")

^{:refer iroh.common/combinations :added "0.1.10"}
(fact "combinations")

^{:refer iroh.common/all-subsets :added "0.1.10"}
(fact "all-subsets")

^{:refer iroh.common/conj-fn :added "0.1.10"}
(fact "conj-fn")

^{:refer iroh.common/context-class :added "0.1.10"}
(fact "context-class")