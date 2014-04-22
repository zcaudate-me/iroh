(ns iroh.pretty.args-test
  (:use midje.sweet)
  (:require [iroh.pretty.args :refer :all]))

^{:refer iroh.pretty.args/args-classify :added "0.1.10"}
(fact "args-classify")

^{:refer iroh.pretty.args/args-convert :added "0.1.10"}
(fact "args-convert")

^{:refer iroh.pretty.args/args-group :added "0.1.10"}
(fact "args-group")