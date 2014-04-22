(ns iroh.core.query-class-test
  (:use midje.sweet)
  (:require [iroh.core.query-class :refer :all]))

^{:refer iroh.core.query-class/all-class-members :added "0.1.10"}
(fact "all-class-members")

^{:refer iroh.core.query-class/list-class-elements :added "0.1.10"}
(fact "list-class-elements")

^{:refer iroh.core.query-class/.? :added "0.1.10"}
(fact ".?")