(ns iroh.test-pretty-display
  (:use midje.sweet)
  (:require [iroh.pretty.display :refer :all]))
  
(fact "has-name?"
  (has-name? #"get" "getString")
  => true

  (has-name? #"to" "getString")
  => false

  (has-name? "getString" "getString")
  => true

  (has-name? "toString" "getString")
  => false)

(fact "has-params?"
  (has-params? [Integer/TYPE] [Integer/TYPE])
  => true

  (has-params? ["byte[][]"] [(Class/forName "[[B")])
  => true

  (has-params? ["[J"] [(Class/forName "[J")])
  => true

  (has-params? #{Integer/TYPE String} [Integer/TYPE])
  => true

  (has-params? #{Integer/TYPE String} [String Integer/TYPE])
  => true)

(fact "has-modifier?"
  (has-modifier? :static #{:static})
  => true)

(fact "has-type?"
  (has-type? "java.lang.Object[][]" (Class/forName "[[Ljava.lang.Object;"))
  => true)

(fact "filter-elements"
  (filter-elements {:modifiers [:static]}
                   [{:modifiers #{:static}}])
  => '({:modifiers #{:static}})

  (filter-elements {:modifiers [:public]}
                   [{:modifiers #{:static}}])
  => ())
