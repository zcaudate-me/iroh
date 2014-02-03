(ns iroh.test-pretty-class
  (:use midje.sweet)
  (:require [iroh.pretty.class :refer :all]))

(fact "primitive types"
  (.getName Void/TYPE) => "void")

(fact "array-name"
  (array-name "[C") => "char[]"
  (array-name "[J") => "long[]"
  (array-name "[[[D") => "double[][][]"
  (array-name "[Ljava.lang.Object;") => "java.lang.Object[]"
  (array-name "[[Ljava.lang.String;") => "java.lang.String[][]")


(fact "simple-name"
  (simple-name "[C") => "char[]"
  (simple-name "char") => "char"
  (simple-name "java.lang.Object") => "java.lang.Object")

(fact "class-name"
  (class-name Object) => "java.lang.Object"
  (class-name (Class/forName "[Z")) => "bool[]")

(fact "class-string"
  (class-string "char") => "char")

(fact "class-from-string"
  (class-from-string "char") => Character/TYPE
  (class-from-string "void") => Void/TYPE)

(fact "create-class"
  (create-class "char") => Character/TYPE
  (create-class "char[]") => (Class/forName "[C")
  (create-class Object) => Object)
