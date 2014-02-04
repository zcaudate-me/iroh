(ns iroh.test-core
  (:use midje.sweet)
  (:require [iroh.core :refer :all]))


(try ((first (.? java.lang.Class :constructor :private)))
     (catch Throwable t))

((first (.? java.lang.Object :private)))

(:params (first (.? Object)))
(comment
  (>pst))
((.? Integer "parseInt" 2) "2")
