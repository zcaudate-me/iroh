(ns midje-doc.iroh-walkthrough
  (:require [iroh.core :refer :all]
            [iroh.hierarchy :refer :all]
            [midje.sweet :refer :all])
  (:refer-clojure :exclude [.> .* .? .% .%> >ns >var]))

[[:chapter {:title "Walkthrough"}]]

"Say I wanted to test all methods for clojure.lang.PersistentHashMap"

(>var hash-without [(type {}) without]
      hash-assoc [clojure.lang.IPersistentMap assoc])

(.? clojure.lang.IPersistentMap "assoc" :#)
(clojure.repl/doc hash-without)
;;=> -------------------------
;;   midje-doc.iroh-walkthrough/hash-without
;;   ([clojure.lang.PersistentArrayMap java.lang.Object])
;;   ------------------
;;
;;   member: clojure.lang.PersistentArrayMap/without
;;   type: clojure.lang.IPersistentMap
;;   modifiers: instance, method, public

(str hash-without)
;; => "#[without :: (clojure.lang.PersistentArrayMap, java.lang.Object) -> clojure.lang.IPersistentMap]"

(origins (:delegate hash-without))

(.? clojure.lang.IPersistentMap "without")


(hash-without {:a 1 :b 2} :a)
;; => {:b 2}

(str hash-assoc)

(hash-assoc {:a 1 :b 2} :c 3)
 ;;=> {:a 1, :b 2, :c 3}


(>ns test.string String :private)
;; => [#'test.string/HASHING_SEED #'test.string/checkBounds
;;     #'test.string/hash #'test.string/hash32
;;     #'test.string/indexOfSupplementary
;;     #'test.string/lastIndexOfSupplementary
;;     #'test.string/serialPersistentFields #'test.string/serialVersionUID
;;     #'test.string/value]

(seq (test.string/value "hello"))
;;=> (\h \e \l \l \o)


[[:section {:title "Overview"}]]

"`iroh` thinks of a class as a container of elements. These elements can be grouped as `fields` or `methods` with membership being `static` or `instance`. Constructors are considered to be static methods with a name of `new`. Inner classes are not contained by the parent class in order to eliminate complexity."

(comment
  (use 'iroh.core))

"The api consists of the following macros:"

(comment
  .> - for showing type hierarchy
  .? - for showing class elements
  .* - for showing instance elements
  .$ - for reflective invocation of objects
  >var - for importing elements into current namespace
  >ns - for importing object elements into a namespace)

[[:section {:title "Type Hierarchy"}]]

"Instead of using `type` which only gives the parent class or `ancestors` which gives all base classes and interfaces in a set, An object's hierachy can been presented using `.`*>*:"
(facts
  (.%> 1)
  => [java.lang.Long
      [java.lang.Number #{java.lang.Comparable}]
      [java.lang.Object #{java.io.Serializable}]]

  (.%> "hello")
  => [java.lang.String
      [java.lang.Object #{java.lang.CharSequence
                          java.io.Serializable
                          java.lang.Comparable}]]
  (.%> {})
  => [clojure.lang.PersistentArrayMap
      [clojure.lang.APersistentMap #{clojure.lang.IObj
                                     clojure.lang.IEditableCollection}]
      [clojure.lang.AFn #{clojure.lang.MapEquivalence
                          clojure.lang.IHashEq
                          java.io.Serializable
                          clojure.lang.IPersistentMap
                          java.util.Map
                          java.lang.Iterable}]
      [java.lang.Object #{clojure.lang.IFn}]])

[[:section {:title "Class Elements"}]]

"`.?` returns the traditional listing of class members. Lets look at all elements in `java.lang.String`:"

(comment
  (.? String)
  =>  ;; (#[CASE_INSENSITIVE_ORDER :: <java.lang.String> | java.util.Comparator]
      ;;     ...
      ;;     ...
      ;;  #[valueOf :: (double) -> java.lang.String])
  )

(facts
  "It can be seen that `.?` returns a whole bunch of elements."

  (-> (.? String) count)
  => 97)


"Filtering can be done depending on the arguments to `.?`, adding \"`charAt`\" will return only elements with the name `charAt`. As can be seen, there is only one element:"

(facts
  (str (.? String "charAt"))
  => "(#[charAt :: (java.lang.String, int) -> char])")

[[:subsection {:title "Application"}]]

"`:#` is used as a convienience argument to grab the first element from the returned list. In the following example, It is assigned to `char-at`:"

(def char-at (.? String "charAt" :#))

(facts
  "`char-at` is an method element. It can be turned into a string:"
  (str char-at)
  => "#[charAt :: (java.lang.String, int) -> char]")

"From the string, it implies that `char-at` is invokable. The element takes in a `String` and an `int`, returning a `char`. It can be used like any other clojure function."
(fact
  (char-at "hello" 0) => \h

  (mapv #(char-at "hello" %) (range 5))  => [\h \e \l \l \o])

"Data for char-at is accessed using keyword lookups:"

(fact
  (:params char-at) => [java.lang.String Integer/TYPE]

  (:modifiers char-at) => #{:instance :method :public}

  (:type char-at)  => Character/TYPE)


(facts
  "The full datastructure can be accessed via the `:all` keyword"

  (:all char-at)
  => (just {:tag :method
            :name "charAt"
            :modifiers #{:instance :method :public},
            :origins [CharSequence String]
            :hash number?
            :container String
            :static false
            :params [String Integer/TYPE]
            :type Character/TYPE
            :delegate #(instance? java.lang.reflect.Method %)}))

"Looking at `char-at` isn't really that interesting, a faster version of `char-at` can be specified much more easily:"

(defn char-at [^String s ^long i]
  (.charAt s i))

(fact
  (char-at "hello" 0) => \h)

[[:subsection {:title "Exposing Privates"}]]

"Private class members and fields can be exposed as easily as public ones. First, a list of private methods defined in Integers are listed:"

(facts
  (.? Integer :private :method :name)
  => ["toUnsignedString"])

"Since there is only `toUnsignedString`, it will be extracted:"

(def unsigned-str (.? Integer "toUnsignedString" :#))

(facts
  "As can be seen by its modifiers, `unsigned-str` is private static method:"

  (:modifiers unsigned-str)
  => #{:method :private :static}

  "The string representation shows that it takes two ints and returns a String:"

  (str unsigned-str)
  => "#[toUnsignedString :: (int, int) -> java.lang.String]")

"The element can now be used, just like a normal function:"

(fact
  (unsigned-str 10 1)
  => "1010"

  (mapv #(unsigned-str 32 (inc %)) (range 6))
  => ["100000" "200" "40" "20" "10" "w"])


[[:subsection {:title "Changing Finals"}]]

"During testing java classes, it is very convenient that final fields can be changed. The example below shows how the value of a String can be changed. Firstly the var `str-value` is set containing the `value` field element of the String class."

(def str-value (.? String "value" :#))

"It can be seen that the element has modifiers private static, meaning that it is usually not accessible, let alone changed."

(fact
  (str str-value) => "#[value :: (java.lang.String) | char[]]"

  (:modifiers str-value) => #{:instance :field :private :final})

"Var `a` is defined, and it `value` field can be accessed:"

(fact
  (def a "hello")

  (seq (str-value a)) => [\h \e \l \l \o])

"Field elements also allow values to be set. In this case, `a.value` has been set to `world`:"

(str-value a (char-array "world"))

(fact
  (seq (str-value a)) => [\w \o \r \l \d]

  a => "world")

[[:section {:title "Filtering"}]]

"There are many filters that can be used with `.?`:
 - regexes and strings for filtering of element names
 - symbols and classes for filtering of return type
 - vectors for filtering of input types
 - longs for filtering of input argment count
 - keywords for filtering of element modifiers
 - keywords for customization of return types"


(facts
  "All the method names in String beginning with `c`"
  (.? String  #"^c" :name)
  => ["charAt" "checkBounds" "codePointAt" "codePointBefore"
      "codePointCount" "compareTo" "compareToIgnoreCase"
      "concat" "contains" "contentEquals" "copyValueOf"]

  "All the private element names in String beginning with `c`"
  (.? String  #"^c" :name :private)
  => ["checkBounds"]

  "All the public field names in String"
  (.? String :name :public :field)
  => ["CASE_INSENSITIVE_ORDER"]

  "All the private field names in String"
  (.? String :name :private :field)
  => ["HASHING_SEED" "hash" "hash32" "serialPersistentFields"
      "serialVersionUID" "value"]

  "All the private static field names in String"
  (.? String :name :private :field :static)
  => ["HASHING_SEED" "serialPersistentFields" "serialVersionUID"]

  "All the private non-static field names in String"
  (.? String :name :private :field :instance)
  => ["hash" "hash32" "value"]
  )

[[:chapter {:title "Instance Elements"}]]

" There is a distinction between `static elements` and `non-static elements` (which we define as `instance elements`)."

(.* "oe" #"hash" :name :modifiers :params :container)

[{:params [java.lang.String],
   :name "hash",
   :modifiers #{:instance :field :private}}
  {:params [java.lang.String],
   :name "hash32",
   :modifiers #{:instance :method :plain}}
  {:params [java.lang.String],
   :name "hash32",
   :modifiers #{:instance :field :transient :private}}
  {:params [java.lang.Object],
   :name "hashCode",
   :modifiers #{:native :instance :method :public}}
  {:params [java.lang.String],
   :name "hashCode",
   :modifiers #{:instance :method :public}}]

(.isAssignableFrom clojure.lang.PersistentHashMap java.util.Map)
 ;;=> false

(.isAssignableFrom java.util.Map clojure.lang.PersistentHashMap)
 ;;=> true

(comment
  (def.import empty-hash [clojure.lang.PersistentHashMap EMPTY])

  (.? clojure.lang.PersistentHashMap)


  (macroexpand-1
   '(>ns
      without2 [clojure.lang.IPersistentMap without]))
  => (clojure.core/let [var (def without2 (iroh.core/.? clojure.lang.IPersistentMap "without" :#))] (clojure.core/alter-meta! var (clojure.core/fn [m] (clojure.core/merge m (iroh.core/element-meta without2)))) var)

  (without2 {:a 1} :a)

  (let)

  ((.? clojure.lang.IPersistentMap "without" :#)
   {:a 1} :a) => {}

   (.? clojure.lang.ISeq)
   (:all without1)
   (without1 {:a 1} :a)
   (>pst)

   (def.extract test.string String)
















































)
