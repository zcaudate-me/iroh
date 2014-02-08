(ns iroh.test-pretty-args
  (:use midje.sweet)
  (:require [iroh.pretty.args :refer :all]))

(fact "args-group"
  (args-group [:public :static 'int '[int]])
  => {:modifiers [:public :static],
      :type [Integer/TYPE]
      :params [[Integer/TYPE]]}

  (args-group [:public :static 'int])

  (args-group [[Class]])
  => {:params [[java.lang.Class]]}

  (args-group [[:any Class]])
  => {:any-params [[:any java.lang.Class]]}

  (args-group [3])
  => {:num-params [3]}

  (args-group '[Class])
  => {:type '[Class]}

  (args-group [Class])
  => {:type [java.lang.Class]})
