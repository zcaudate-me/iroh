(ns iroh.java.util-test
  (:use midje.sweet)
  (:require [iroh.common :refer [class-array]]
            [iroh.util :as util])
  (:import im.chit.iroh.Util))

(fact "boxArg"
  (Util/boxArg Float/TYPE 2)
  => 2.0

  (Util/boxArg Integer/TYPE 2.001)
  => 2

  (type (Util/boxArg Short/TYPE 1.0))
  => java.lang.Short)

(fact "boxArg"
  (util/box-arg Float/TYPE 2)
  => 2.0

  (util/box-arg Integer/TYPE 2.001)
  => 2

  (type (util/box-arg Short/TYPE 1.0))
  => java.lang.Short)


(fact "paramArgTypeMatch basics"
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

(fact
 (util/param-arg-match Double/TYPE Float/TYPE)
 => true

 (util/param-arg-match Float/TYPE Double/TYPE)
 => true

 (util/param-arg-match Integer/TYPE Float/TYPE)
 => false

 (util/param-arg-match Byte/TYPE Long/TYPE)
 => false

 (util/param-arg-match Long/TYPE Byte/TYPE)
 => true

 (util/param-arg-match Long/TYPE Long)
 => true

 (util/param-arg-match Long Byte)
 => false

 (util/param-arg-match clojure.lang.PersistentHashMap java.util.Map)
 => false

 (util/param-arg-match java.util.Map clojure.lang.PersistentHashMap)
 => true)
