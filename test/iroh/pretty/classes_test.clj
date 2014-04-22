(ns iroh.pretty.classes-test
  (:use midje.sweet)
  (:require [iroh.pretty.classes :refer :all]))

^{:refer iroh.pretty.classes/type->raw :added "0.1.10"}
(fact "type->raw")

^{:refer iroh.pretty.classes/raw-array->string :added "0.1.10"}
(fact "raw-array->string")

^{:refer iroh.pretty.classes/raw->string :added "0.1.10"}
(fact "raw->string")

^{:refer iroh.pretty.classes/string-array->raw :added "0.1.10"}
(fact "string-array->raw")

^{:refer iroh.pretty.classes/string->raw :added "0.1.10"}
(fact "string->raw")

^{:refer iroh.pretty.classes/class-convert-impl :added "0.1.10"}
(fact "class-convert-impl")

^{:refer iroh.pretty.classes/class-convert :added "0.1.10"}
(fact "class-convert")

^{:refer iroh.pretty.classes/class-convert-strin :added "0.1.10"}
(fact "class-convert-strin")