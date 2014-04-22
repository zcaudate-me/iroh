(ns iroh.hierarchy-test
  (:use midje.sweet)
  (:require [iroh.hierarchy :refer :all]))

^{:refer iroh.hierarchy/interface? :added "0.1.10"}
(fact "Tests whether `x` is a java interface."

  (interface? java.util.Map)
  => true

  (interface? Class)
  => false)

^{:refer iroh.hierarchy/abstract? :added "0.1.10"}
(fact "Test whether `x` is abstract."

  (abstract? java.util.Map)
  => true

  (abstract? Class)
  => false)

^{:refer iroh.hierarchy/inheritance-list :added "0.1.10"}
(fact "Lists all direct superclasses of `cls`"

  (inheritance-list String)
  => [java.lang.Object java.lang.String]

  (inheritance-list clojure.lang.PersistentHashMap)
  => [java.lang.Object clojure.lang.AFn
      clojure.lang.APersistentMap
      clojure.lang.PersistentHashMap])

^{:refer iroh.hierarchy/base-list :added "0.1.10"}
(fact "Lists all direct superclasses and interfaces of `cls`"
  (base-list String)
  => [[java.lang.Object
       #{java.lang.CharSequence
         java.lang.Comparable
         java.io.Serializable}]]

  ^:hidden
  (base-list Class)
  => [[java.lang.Object
       #{java.io.Serializable
         java.lang.reflect.Type
         java.lang.reflect.AnnotatedElement
         java.lang.reflect.GenericDeclaration}]]

  (base-list clojure.lang.PersistentHashMap)
  => [[clojure.lang.APersistentMap
       #{clojure.lang.IEditableCollection
         clojure.lang.IObj}]
      [clojure.lang.AFn
       #{java.io.Serializable
         clojure.lang.IHashEq
         clojure.lang.MapEquivalence
         java.util.Map
         java.lang.Iterable
         clojure.lang.IPersistentMap}]
      [java.lang.Object
       #{clojure.lang.IFn}]])

(def without-method
    (-> clojure.lang.PersistentArrayMap
        (.getDeclaredMethod "without"
                            (iroh.common/class-array [Object]))))

^{:refer iroh.hierarchy/has-method :added "0.1.10"}
(fact "Checks to see if any given method exists in a particular class"

  (has-method without-method
              String)
  => nil

  (has-method without-method
              clojure.lang.PersistentArrayMap)
  => clojure.lang.PersistentArrayMap)

^{:refer iroh.hierarchy/methods-with-same-name-and-count :added "0.1.10"}
(fact "methods-with-same-name-and-count"

  (methods-with-same-name-and-count without-method clojure.lang.IPersistentMap)
  =>  #(-> % count (= 1))  ;; (#<Method clojure.lang.IPersistentMap.without(java.lang.Object)>)

  ^:hidden
  (methods-with-same-name-and-count
   (.getDeclaredMethod String "charAt"
                       (iroh.common/class-array Class [Integer/TYPE]))
   CharSequence)
  =>
  #(-> % count (= 1))  ;; (#<Method java.lang.CharSequence.charAt(int)>)
)

^{:refer iroh.hierarchy/has-overridden-method :added "0.1.10"}
(fact "Checks to see that the method can be "

  (has-overridden-method without-method String)
  => nil

  (has-overridden-method without-method clojure.lang.IPersistentMap)
  => clojure.lang.IPersistentMap)

^{:refer iroh.hierarchy/origins :added "0.1.10"}
(fact "Lists all the classes tha contain a particular method"

  (def without-method
    (-> clojure.lang.PersistentArrayMap
        (.getDeclaredMethod "without"
                            (iroh.common/class-array [Object]))))

  (origins without-method)
  => [clojure.lang.IPersistentMap
      clojure.lang.PersistentArrayMap])
