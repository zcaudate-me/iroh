(ns iroh.pretty.display.sort-test
  (:use midje.sweet)
  (:require [iroh.pretty.display.sort :refer :all]))

^{:refer iroh.pretty.display.sort/sort-fn :added "0.1.10"}
(fact "sort-fn")

^{:refer iroh.pretty.display.sort/sort-terms-fn :added "0.1.10"}
(fact "sort-terms-fn")