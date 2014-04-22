(ns iroh.core.delegate-test
  (:use midje.sweet)
  (:require [iroh.core.delegate :refer :all]))

^{:refer iroh.core.delegate/delegate :added "0.1.10"}
(fact "delegate")