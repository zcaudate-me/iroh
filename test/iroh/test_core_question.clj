(ns iroh.test-core-question
  (:use midje.sweet)
  (:require [iroh.core :refer :all]
            [iroh.types.element :refer :all]
            [iroh.pretty.classes :refer [class-convert]])
  (:refer-clojure :exclude [.> .* .? .% .%> >ns >var]))

(fact ".? field"
  (.? String :field :name)
  => '("CASE_INSENSITIVE_ORDER" "HASHING_SEED" "hash" "hash32" "serialPersistentFields" "serialVersionUID" "value")

  (.? String :field :public :name)
  => '("CASE_INSENSITIVE_ORDER")

  (.? String :field :private :name)
  => '("HASHING_SEED" "hash" "hash32" "serialPersistentFields" "serialVersionUID" "value")

  (.? String :field :protected :name)
  => ()

  (.? String :field :plain :name)
  => ())

(fact ".? method"
  (.? String :method :name)
  => '("charAt" "checkBounds" "codePointAt" "codePointBefore" "codePointCount" "compareTo" "compareToIgnoreCase" "concat" "contains" "contentEquals" "copyValueOf" "endsWith" "equals" "equalsIgnoreCase" "format" "getBytes" "getChars" "hash32" "hashCode" "indexOf" "indexOfSupplementary" "intern" "isEmpty" "lastIndexOf" "lastIndexOfSupplementary" "length" "matches" "offsetByCodePoints" "regionMatches" "replace" "replaceAll" "replaceFirst" "split" "startsWith" "subSequence" "substring" "toCharArray" "toLowerCase" "toString" "toUpperCase" "trim" "valueOf")

  (.? String :method :public :name)
  => '("charAt" "codePointAt" "codePointBefore" "codePointCount" "compareTo" "compareToIgnoreCase" "concat" "contains" "contentEquals" "copyValueOf" "endsWith" "equals" "equalsIgnoreCase" "format" "getBytes" "getChars" "hashCode" "indexOf" "intern" "isEmpty" "lastIndexOf" "length" "matches" "offsetByCodePoints" "regionMatches" "replace" "replaceAll" "replaceFirst" "split" "startsWith" "subSequence" "substring" "toCharArray" "toLowerCase" "toString" "toUpperCase" "trim" "valueOf")

  (.? String :method :private :name)
  => '("checkBounds" "indexOfSupplementary" "lastIndexOfSupplementary")

  (.? String :method :protected :name)
  => ()

  (.? String :method :plain :name)
  => '("getChars" "hash32" "indexOf" "lastIndexOf"))

(fact ".? indexOf"
  (.? String :method :plain :name :params)
  => [{:params (map class-convert [String "char[]" "int"]), :name "getChars"}
      {:params (map class-convert [String]), :name "hash32"}
      {:params (map class-convert '["char[]" int int "char[]" int int int])
       :name "indexOf"}
      {:params (map class-convert '["char[]" int int "char[]" int int int]),
       :name "lastIndexOf"}]

  (.? String :method :name :params "indexOf")
  => (just [{:params (map class-convert '["char[]" int int "char[]" int int int]) :name "indexOf"}
            {:params [String String Integer/TYPE] :name "indexOf"}
            {:params [String String] :name "indexOf"}
            {:params [String Integer/TYPE] :name "indexOf"}
            {:params [String Integer/TYPE Integer/TYPE] :name "indexOf"}]
            :in-any-order)

  (.? String "indexOf" :static :name :params)
  => [{:params (map class-convert '["char[]" int int "char[]" int int int])
       :name "indexOf"}]

  (.? String "indexOf" :static :name :params :#)
  => {:params (map class-convert '["char[]" int int "char[]" int int int])
      :name "indexOf"}

  (.? String "indexOf" :static :# :name :params)
  => (.? String "indexOf" :static :name :params :first))


(fact ".? Hashmap"
  ((.? clojure.lang.IPersistentMap "assoc" :#) {} :key :value)
  => {:key :value}

  ((.? java.util.Map "containsKey" :#) {:x 1} :x)
  => true

  ((.? clojure.lang.IPersistentMap "without" :#) {:x 1} :x)
  => {})

(fact ".? ILookup"
  ((.? clojure.lang.ILookup 2 :#) {:x :1} :x)
  => :1

  ((.? clojure.lang.ILookup 3 :#) {:x :1} :x :NAN)
  => :1

  ((.? clojure.lang.ILookup 3 :#) {:x :1} :y :NAN)
  => :NAN)

(fact ".? Math"
  (>= (-> (.? java.lang.Math) count)
      (-> (.? java.lang.Math [:any 'double]) count)
      (-> (.? java.lang.Math ['double]) count))
  => true)

;;(meta #'to-boolean)
(comment
  ((.* java.lang.Boolean "toBoolean" :#) "true")

  (def to-boolean (.* java.lang.Boolean "toBoolean" :#))

  (get (ns-publics *ns*) (symbol "to-boolean"))



  (element-meta to-boolean)
  (def to-bool (.* Boolean "toBoolean" :#))

  (def to-bool (.? Object "toString"))


  (defmacro def.extract
    )

  (def.import
    to-boolean [Boolean toBoolean]
    to-string  [Object  toString])

  (def.extract Object test.object)

  (to-stri)

  (defn add "oeuoeu "([a m]))
  (meta #'add)
  (def.import to-boolean [Boolean toBoolean])
  (def.n to-string  [Object hashCode])
  (def.n to-string  [Long serialVersionUID])



  (meta #'to-boolean)
  (to-boole)
  (defnjava to-boolean [Boolean toBoolean])
  (>refresh)

  (to-boolean "true")

  (Boolean/toBoolean "true")
  :origins)


(comment
  (>pst)
  (:params (first (.? Object)))
  )
