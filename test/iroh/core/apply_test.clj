(ns iroh.core.apply-test
  (:use midje.sweet)
  (:require [iroh.core.apply :refer :all]))


^{:refer iroh.core.apply/.> :added "0.1.10"}
(fact "Threads the first input into the rest of the functions. Same as `->` but
   allows access to private fields using both `:keyword` and `.symbol` lookup:"

  (.> "abcd" :value String.) => "abcd"

  (.> "abcd" .value String.) => "abcd"

  (let [a  "hello"
        _  (.> a (.value (char-array "world")))]
    a)
  => "world")
