(ns iroh.test-java-util
  (:use midje.sweet)
  (:require [iroh.common :refer [class-array]])
  (:import im.chit.iroh.Util))

(fact "boxArg"
  (Util/boxArg Float/TYPE 2)
  => 2.0

  (Util/boxArg Integer/TYPE 2.001)
  => 2

  (type (Util/boxArg Short/TYPE 1.0))
  => java.lang.Short)

(fact "boxArgs"
  (seq (Util/boxArgs (class-array Class [Integer/TYPE Float/TYPE])
                     (object-array [1.0 2.0])))
  => '(1 2.0))

(fact "paramArgTypeMatch"
  (.isPrimitive Integer/TYPE)
  => true

  (.isAssignableFrom Integer/TYPE Long/TYPE)
  => false

  (.isAssignableFrom Long/TYPE Integer/TYPE)
  => false

  (.isAssignableFrom java.util.Map clojure.lang.PersistentHashMap)
  => true

  (.isAssignableFrom clojure.lang.PersistentHashMap java.util.Map)
  => false)
