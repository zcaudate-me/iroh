(ns iroh.core.query-instance-test
  (:use midje.sweet)
  (:require [iroh.core.query-instance :refer :all]))

^{:refer iroh.core.query-instance/all-instance-elements :added "0.1.10"}
(fact "all-instance-elements")

^{:refer iroh.core.query-instance/list-instance-elements :added "0.1.10"}
(fact "list-instance-elements")

^{:refer iroh.core.query-instance/.* :added "0.1.10"}
(fact ".*")