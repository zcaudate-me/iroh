(ns iroh.pretty.display.filter-test
  (:use midje.sweet)
  (:require [iroh.pretty.display.filter :refer :all]))

^{:refer iroh.pretty.display.filter/has-predicate? :added "0.1.10"}
(fact "has-predicate?")

^{:refer iroh.pretty.display.filter/has-name? :added "0.1.10"}
(fact "has-name?")

^{:refer iroh.pretty.display.filter/has-modifier? :added "0.1.10"}
(fact "has-modifier?")

^{:refer iroh.pretty.display.filter/has-params? :added "0.1.10"}
(fact "has-params?")

^{:refer iroh.pretty.display.filter/has-num-params? :added "0.1.10"}
(fact "has-num-params?")

^{:refer iroh.pretty.display.filter/has-any-params? :added "0.1.10"}
(fact "has-any-params?")

^{:refer iroh.pretty.display.filter/has-all-params? :added "0.1.10"}
(fact "has-all-params?")

^{:refer iroh.pretty.display.filter/has-type? :added "0.1.10"}
(fact "has-type?")

^{:refer iroh.pretty.display.filter/has-origins? :added "0.1.10"}
(fact "has-origins?")

^{:refer iroh.pretty.display.filter/filter-by :added "0.1.10"}
(fact "filter-by")

^{:refer iroh.pretty.display.filter/filter-terms-fn :added "0.1.10"}
(fact "filter-terms-fn")