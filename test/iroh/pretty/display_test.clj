(ns iroh.pretty.display-test
  (:use midje.sweet)
  (:require [iroh.pretty.display :refer :all]))

^{:refer iroh.pretty.display/first-terms-fn :added "0.1.10"}
(fact "first-terms-fn")

^{:refer iroh.pretty.display/merge-terms-fn :added "0.1.10"}
(fact "merge-terms-fn")

^{:refer iroh.pretty.display/select-terms-fn :added "0.1.10"}
(fact "select-terms-fn")

^{:refer iroh.pretty.display/display :added "0.1.10"}
(fact "display")