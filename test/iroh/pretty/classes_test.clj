(ns iroh.pretty.classes-test
  (:use midje.sweet)
  (:require [iroh.pretty.classes :refer :all]))

^{:refer iroh.pretty.classes/type->raw :added "0.1.10"}
(fact "converts to the raw representation"

  (type->raw Class) => "java.lang.Class"
  (type->raw 'byte) => "B")

^{:refer iroh.pretty.classes/raw-array->string :added "0.1.10"}
(fact "converts the raw representation to a more readable form"

  (raw-array->string "[[B") => "byte[][]"
  (raw-array->string "[Ljava.lang.Class;") => "java.lang.Class[]")

^{:refer iroh.pretty.classes/raw->string :added "0.1.10"}
(fact "converts the raw array representation to a human readable form"

  (raw->string "[[V") => "void[][]"
  (raw->string "[Ljava.lang.String;") => "java.lang.String[]")

^{:refer iroh.pretty.classes/string-array->raw :added "0.1.10"}
(fact "converts the human readable form to a raw string"

  (string-array->raw "java.lang.String[]")"[Ljava.lang.String;")

^{:refer iroh.pretty.classes/string->raw :added "0.1.10"}
(fact "converts any string to it's raw representation"

  (string->raw "java.lang.String[]") => "[Ljava.lang.String;"

  (string->raw "int[][][]") => "[[[I")

^{:refer iroh.pretty.classes/class-convert-impl :added "0.1.10"}
(fact "converts a string to its representation. Implementation function"

  (class-convert-impl Class  :string) => "java.lang.Class"

  (class-convert-impl "byte" :class) => Byte/TYPE

  (class-convert-impl "byte" :container) => Byte)

^{:refer iroh.pretty.classes/class-convert :added "0.1.10"}
(fact "Converts a class to its representation."

  (class-convert "byte") => Byte/TYPE

  (class-convert 'byte :string) => "byte"

  (class-convert (Class/forName "[[B") :string) => "byte[][]")
