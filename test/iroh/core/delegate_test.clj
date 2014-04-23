(ns iroh.core.delegate-test
  (:use midje.sweet)
  (:require [iroh.core.delegate :refer :all]))

^{:refer iroh.core.delegate/delegate :added "0.1.10"}
(fact "Allow transparent field access and manipulation to the underlying object."

  (let [a   "hello"
        >a  (delegate a)]

    (keys >a) => (just [:hash :hash32 :value] :in-any-order)

    (seq (>a :value)) => [\h \e \l \l \o]

    (>a :value (char-array "world"))
    a => "world"))
