(ns iroh.core.apply-test
  (:use midje.sweet)
  (:require [iroh.core.apply :refer :all]))

^{:refer iroh.core.apply/instance-lookup-path :added "0.1.10"}
(fact "instance-lookup-path")

^{:refer iroh.core.apply/assignable? :added "0.1.10"}
(fact "assignable?")

^{:refer iroh.core.apply/instance-lookup :added "0.1.10"}
(fact "instance-lookup")

^{:refer iroh.core.apply/object-lookup :added "0.1.10"}
(fact "object-lookup")

^{:refer iroh.core.apply/refine-lookup :added "0.1.10"}
(fact "refine-lookup")

^{:refer iroh.core.apply/apply-vector :added "0.1.10"}
(fact "apply-vector")

^{:refer iroh.core.apply/get-element-lookup :added "0.1.10"}
(fact "get-element-lookup")

^{:refer iroh.core.apply/apply-element :added "0.1.10"}
(fact "apply-element")

^{:refer iroh.core.apply/.> :added "0.1.10"}
(fact ".>")