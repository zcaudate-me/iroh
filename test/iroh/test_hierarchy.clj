(ns iroh.test-hierarchy
  (:use midje.sweet)
  (:require [iroh.common :refer :all]
            [iroh.hierarchy :refer :all]))

(fact "interface?"
  (interface? java.util.Map) => true
  (interface? Class) => false)

(fact "abstract"
  (abstract? java.util.Map) => true
  (abstract? Class) => false)

(fact "base-list"
  (base-list Class)
  => [[java.lang.Object #{java.io.Serializable
                          java.lang.reflect.Type
                          java.lang.reflect.AnnotatedElement
                          java.lang.reflect.GenericDeclaration}]]

  (base-list clojure.lang.PersistentHashMap)
  => [[clojure.lang.APersistentMap #{clojure.lang.IEditableCollection
                                     clojure.lang.IObj}]
      [clojure.lang.AFn #{java.io.Serializable
                          clojure.lang.IHashEq
                          clojure.lang.MapEquivalence
                          java.util.Map
                          java.lang.Iterable
                          clojure.lang.IPersistentMap}]
      [java.lang.Object #{clojure.lang.IFn}]])

(fact "origins"
  (origins (.getDeclaredMethod Object "toString"
                               (class-array Class [])))
  => [java.lang.Object]

  (origins (.getDeclaredMethod clojure.lang.APersistentMap "invoke"
                               (class-array Class [Object Object])))
  => [clojure.lang.IFn clojure.lang.AFn clojure.lang.APersistentMap])
