(ns iroh.test-core
  (:use midje.sweet)
  (:require [iroh.core :refer :all]))


(try ((first (.? java.lang.Class :constructor :private)))
     )
(>pst)

(.? java.lang.Object :method :private)
