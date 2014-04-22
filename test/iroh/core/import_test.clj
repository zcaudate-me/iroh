(ns iroh.core.import-test
  (:use midje.sweet)
  (:require [iroh.core.import :refer :all]))

^{:refer iroh.core.import/element-meta :added "0.1.10"}
(fact "element-meta")

^{:refer iroh.core.import/>var :added "0.1.10"}
(fact ">var")

^{:refer iroh.core.import/>ns :added "0.1.10"}
(fact ">ns")