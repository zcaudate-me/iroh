(ns iroh.test-types-modifiers
  (:use midje.sweet)
  (:require [iroh.types.modifiers :refer :all]))

(fact "int-to-modifiers"
  (int-to-modifiers 2r001100)
  => #{:protected :static}

  (int-to-modifiers 128 :field)
  => #{:transient}

  (int-to-modifiers 128 :method)
  => #{:varargs})


(fact "modifiers-to-int"
 (modifiers-to-int #{:protected :static})
 => 12

 (modifiers-to-int #{:transient :field})
 => 128)
