(ns iroh.element.field-test
  (:use midje.sweet)
  (:require [iroh.element.field :refer :all]))

^{:refer iroh.element.field/arg-params :added "0.1.10"}
(fact "arg-params")

^{:refer iroh.element.field/throw-arg-exception :added "0.1.10"}
(fact "throw-arg-exception")

^{:refer iroh.element.field/invoke-static-field :added "0.1.10"}
(fact "invoke-static-field")

^{:refer iroh.element.field/invoke-instance-field :added "0.1.10"}
(fact "invoke-instance-field")