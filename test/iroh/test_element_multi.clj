(ns iroh.test-element-multi
  (:use midje.sweet)
  (:require [iroh.core :refer :all]
            [iroh.types.element :refer :all])
  (:refer-clojure :exclude [.> .* .? .% .%> >ns >var]))
  
(fact "multiple argument types" 
  ((.? String "new" :#) "hello")
  => "hello"
  
  ((.? String "new" :#) (byte-array (map byte "hello")))
  => "hello"
  
  ((.? String "new" :#) 1)
  => (throws Exception)
  
  
  
  )
  