(ns iroh.types.element-test
  (:use midje.sweet)
  (:require [iroh.types.element :refer :all]))

^{:refer iroh.types.element/invoke-element* :added "0.1.10"}
(fact "invoke-element*")

^{:refer iroh.types.element/invoke-element :added "0.1.10"}
(fact "invoke-element")

^{:refer iroh.types.element/to-element :added "0.1.10"}
(fact "to-element")

^{:refer iroh.types.element/element-params :added "0.1.10"}
(fact "element-params")

^{:refer iroh.types.element/format-element :added "0.1.10"}
(fact "format-element")

^{:refer iroh.types.element/make-invoke-element-form :added "0.1.10"}
(fact "make-invoke-element-form")

^{:refer iroh.types.element/init-element-type :added "0.1.10"}
(fact "init-element-type")

^{:refer iroh.types.element/element :added "0.1.10"}
(fact "element")

^{:refer iroh.types.element/element? :added "0.1.10"}
(fact "element?")