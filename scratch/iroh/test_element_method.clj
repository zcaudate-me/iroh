(ns iroh.test-element-method
  (:use midje.sweet)
  (:require [iroh.common :refer :all]
            [iroh.types.element :refer :all]
            [iroh.element.method :refer :all]))

(def round
  (->> (class-array Class [Float/TYPE])
       (.getDeclaredMethod Math "round")
       (to-element)))

(fact "invoke with arguments"
  (round (double 4.0)) => 4

  (round (int 4.0)) => 4

  (round (byte 4.0)) => 4

  (round \a)
  => (throws Exception "Method `round` expects params to be of type [float], but was invoked with [java.lang.Character] instead")

  (round true)
  => (throws Exception "Method `round` expects params to be of type [float], but was invoked with [java.lang.Boolean] instead"))


#_(def
  (->> (class-array Class [Float/TYPE])
       (.getDeclaredMethod Math "round")
       (to-element)))
