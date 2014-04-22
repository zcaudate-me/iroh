(ns iroh.types.modifiers-test
  (:use midje.sweet)
  (:require [iroh.types.modifiers :refer :all]))

^{:refer iroh.types.modifiers/get-modifiers :added "0.1.10"}
(fact "get-modifiers")

^{:refer iroh.types.modifiers/int-to-modifiers :added "0.1.10"}
(fact "int-to-modifiers")

^{:refer iroh.types.modifiers/modifiers-to-int :added "0.1.10"}
(fact "modifiers-to-int")