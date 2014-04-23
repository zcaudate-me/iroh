(ns iroh.core.query-class-test
  (:use midje.sweet)
  (:require [iroh.core.query-class :refer :all]))

^{:refer iroh.core.query-class/.? :added "0.1.10"}
(fact "queries the java view of the class declaration"

  (.? String  #"^c" :name)
  => ["charAt" "checkBounds" "codePointAt" "codePointBefore"
      "codePointCount" "compareTo" "compareToIgnoreCase"
      "concat" "contains" "contentEquals" "copyValueOf"])
