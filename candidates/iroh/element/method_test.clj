(ns iroh.element.method-test
  (:use midje.sweet)
  (:require [iroh.element.method :refer :all]))

^{:refer iroh.element.method/invoke-static-method :added "0.1.10"}
(fact "invoke-static-method")

^{:refer iroh.element.method/invoke-instance-method :added "0.1.10"}
(fact "invoke-instance-method")

^{:refer iroh.element.method/to-static-method :added "0.1.10"}
(fact "to-static-method")

^{:refer iroh.element.method/to-instance-method :added "0.1.10"}
(fact "to-instance-method")

^{:refer iroh.element.method/to-pre-element :added "0.1.10"}
(fact "to-pre-element")