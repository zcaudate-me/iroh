(ns iroh.hierarchy-test
  (:use midje.sweet)
  (:require [iroh.hierarchy :refer :all]))

^{:refer iroh.hierarchy/interface? :added "0.1.10"}
(fact "interface?")

^{:refer iroh.hierarchy/abstract? :added "0.1.10"}
(fact "abstract?")

^{:refer iroh.hierarchy/inheritance-list :added "0.1.10"}
(fact "inheritance-list")

^{:refer iroh.hierarchy/base-list :added "0.1.10"}
(fact "base-list")

^{:refer iroh.hierarchy/has-method :added "0.1.10"}
(fact "has-method")

^{:refer iroh.hierarchy/methods-with-same-name-and-count :added "0.1.10"}
(fact "methods-with-same-name-and-count")

^{:refer iroh.hierarchy/is-assignable? :added "0.1.10"}
(fact "is-assignable?")

^{:refer iroh.hierarchy/has-overridden-method :added "0.1.10"}
(fact "has-overridden-method")

^{:refer iroh.hierarchy/origins :added "0.1.10"}
(fact "origins")