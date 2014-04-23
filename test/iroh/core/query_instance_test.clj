(ns iroh.core.query-instance-test
  (:use midje.sweet)
  (:require [iroh.core.query-instance :refer :all]))

^{:refer iroh.core.query-instance/.* :added "0.1.10"}
(fact "lists what methods could be applied to a particular instance"

  (.* "abc" :name #"^to")
  => ["toCharArray" "toLowerCase" "toString" "toUpperCase"]

  (.* String :name #"^to")
  => ["toString"])
