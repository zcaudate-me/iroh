(ns iroh.test-core
  (:use midje.sweet)
  (:require [iroh.core :refer :all]))


(.? java.lang.Class :method :private)

(.? java.lang.Object :method :private)
