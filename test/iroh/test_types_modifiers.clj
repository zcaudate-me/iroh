(ns iroh.test-types-modifiers
  (:use midje.sweet)
  (:require [iroh.types.modifiers :refer :all]))

(fact "int-to-modifiers"
  (int-to-modifiers 2r001100)
  => #{:protected :static}

  (modifiers-to-int #{:protected :static})
  => 12)
