(ns iroh.test-pretty-primitives
  (:use midje.sweet)
  (:require [iroh.pretty.primitives :refer :all]))

(fact "primitive-convert"
  (primitive-convert "Z" :symbol)
  => 'boolean

  (primitive-convert "Z" :class)
  => Boolean/TYPE

  (primitive-convert "Z" :string)
  => "boolean"

  (primitive-convert "Z" :raw)
  => nil

  (primitive-convert "Z" :container)
  => java.lang.Boolean)


(fact "primitive-convert"
  (primitive-convert "void" :symbol)
  => 'void

  (primitive-convert "void" :class)
  => Void/TYPE

  (primitive-convert "void" :string)
  => nil

  (primitive-convert "void" :raw)
  => "V"

  (primitive-convert "void" :container)
  => Void)
