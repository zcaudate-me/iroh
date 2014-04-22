(ns iroh.element.common-test
  (:use midje.sweet)
  (:require [iroh.element.common :refer :all]))

^{:refer iroh.element.common/set-accessible :added "0.1.10"}
(fact "set-accessible")

^{:refer iroh.element.common/add-annotations :added "0.1.10"}
(fact "add-annotations")

^{:refer iroh.element.common/seed :added "0.1.10"}
(fact "seed")

^{:refer iroh.element.common/throw-arg-exception :added "0.1.10"}
(fact "throw-arg-exception")

^{:refer iroh.element.common/box-args :added "0.1.10"}
(fact "box-args")

^{:refer iroh.element.common/format-element-method :added "0.1.10"}
(fact "format-element-method")

^{:refer iroh.element.common/element-params-method :added "0.1.10"}
(fact "element-params-method")