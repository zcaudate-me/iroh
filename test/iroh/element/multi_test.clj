(ns iroh.element.multi-test
  (:use midje.sweet)
  (:require [iroh.element.multi :refer :all]))

^{:refer iroh.element.multi/get-name :added "0.1.10"}
(fact "get-name")

^{:refer iroh.element.multi/to-element-array :added "0.1.10"}
(fact "to-element-array")

^{:refer iroh.element.multi/multi-element :added "0.1.10"}
(fact "multi-element")

^{:refer iroh.element.multi/to-element-map-path :added "0.1.10"}
(fact "to-element-map-path")

^{:refer iroh.element.multi/elegible-candidates :added "0.1.10"}
(fact "elegible-candidates")

^{:refer iroh.element.multi/find-method-candidate :added "0.1.10"}
(fact "find-method-candidate")

^{:refer iroh.element.multi/find-field-candidate :added "0.1.10"}
(fact "find-field-candidate")

^{:refer iroh.element.multi/find-candidate :added "0.1.10"}
(fact "find-candidate")