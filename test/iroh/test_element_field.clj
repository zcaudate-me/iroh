(ns iroh.test-element-field
  (:use midje.sweet)
  (:require [iroh.types.element :refer :all]
            [iroh.element.field :refer :all]))

(def ele (to-element (.getDeclaredField java.lang.Integer "TYPE")))

(fact "to-element"
  (str ele)
  => "#[TYPE :: java.lang.Class]"

  (invoke-element ele)
  => Integer/TYPE

  (get ele nil)
  => (just {:type java.lang.Class,
            :name "TYPE",
            :tag :field,
            :hash number?
            :container java.lang.Integer,
            :modifiers #{:field :static :public :final},
            :static true,
            :delegate #(instance? java.lang.reflect.Field %)}))
