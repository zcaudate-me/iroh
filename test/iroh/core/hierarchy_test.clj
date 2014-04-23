(ns iroh.core.hierarchy-test
  (:use midje.sweet)
  (:require [iroh.core.hierarchy :refer :all]))

^{:refer iroh.core.hierarchy/.% :added "0.1.10"}
(fact "Lists class information"

  (.% String)  ;; or (.%> "")
  => (contains {:name "java.lang.String"
                :tag :class
                :hash anything
                :container nil
                :modifiers #{:instance :class :public :final}
                :static false
                :delegate java.lang.String}))

^{:refer iroh.core.hierarchy/.%> :added "0.1.10"}
(fact "Lists the class and interface hierarchy for the class"

  (.%> String)   ;; or (.%> "")
  => [java.lang.String
      [java.lang.Object
       #{java.io.Serializable
         java.lang.Comparable
         java.lang.CharSequence}]])
