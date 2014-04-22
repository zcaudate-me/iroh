(ns iroh.pretty.primitives-test
  (:use midje.sweet)
  (:require [iroh.pretty.primitives :refer :all]))

^{:refer iroh.pretty.primitives/primitive-convert :added "0.1.10"}
(fact "Converts primitive values across their different representations. The choices are:
   :raw       - The string in the jdk (i.e. `Z` for Boolean, `C` for Character)
   :symbol    - The symbol that iroh uses for matching (i.e. boolean, char, int)
   :string    - The string that iroh uses for matching
   :class     - The primitive class representation of the primitive
   :container - The containing class representation for the primitive type"

  (primitive-convert Boolean/TYPE :symbol)
  => 'boolean

  (primitive-convert "Z" :symbol)
  => 'boolean

  (primitive-convert "int" :symbol)
  => 'int

  (primitive-convert Character :string)
  => "char"

  (primitive-convert "V" :class)
  => Void/TYPE

  (primitive-convert 'long :container)
  => Long)
