(ns iroh.test-pretty-primitives
  (:use midje.sweet)
  (:require [iroh.pretty.primitives :refer :all]))

(fact "primitive-convert"
  (primitive-convert "Z" :symbol)
  => 'boolean

  (primitive-convert "Z" :type)
  => Boolean/TYPE

  (primitive-convert "Z" :string)
  => "boolean"

  (primitive-convert "Z" :raw)
  => nil)


(fact "primitive-convert"
  (primitive-convert "void" :symbol)
  => 'void

  (primitive-convert "void" :type)
  => Void/TYPE

  (primitive-convert "void" :string)
  => nil

  (primitive-convert "void" :raw)
  => "V")
