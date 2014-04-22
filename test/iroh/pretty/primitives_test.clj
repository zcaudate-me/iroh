(ns iroh.pretty.primitives-test
  (:use midje.sweet)
  (:require [iroh.pretty.primitives :refer :all]))

^{:refer iroh.pretty.primitives/primitive-convert :added "0.1.10"}
(fact "primitive-convert")