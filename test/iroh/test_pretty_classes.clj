(ns iroh.test-pretty-classes
  (:use midje.sweet)
  (:require [iroh.pretty.classes :refer :all]))


(fact "type->raw"
  (type->raw Boolean/TYPE)
  => "Z"

  (type->raw (Class/forName "[Z"))
  => "[Z"

  (type->raw (Class/forName "[[[[Z"))
  => "[[[[Z"

  (type->raw Object)
  => "java.lang.Object"

  (type->raw (Class/forName "[[[[Ljava.lang.Object;"))
  => "[[[[Ljava.lang.Object;")

(fact "raw->string"
  (raw->string "Z")
  => "boolean"

  (raw->string "[Z")
  => "boolean[]"

  (raw->string "[[[[C")
  => "char[][][][]"

  (raw->string "[[[[Ljava.lang.String")
  => "Ljava.lang.String[][][][]")

(fact "string->raw"
  (string->raw "char")
  => "C"

  (string->raw "C")
  => "C"

  (string->raw "char[][][][]")
  => "[[[[C"

  (string->raw "java.lang.Object")
  => "java.lang.Object"

  (string->raw "java.lang.Object[][][][]")
  => "[[[[Ljava.lang.Object;")


(fact "class-convert"
  (class-convert "[C" :class)
  =>(Class/forName "[C")

  (class-convert "char[][]" :class)
  => (Class/forName "[[C")

  (class-convert "char[][]" :symbol)
  => (symbol "[[C")

  (class-convert "java.lang.Object[][]" :symbol)
  => (symbol "[[Ljava.lang.Object;")

  (class-convert "java.lang.Object[][]" :class)
  => (Class/forName "[[Ljava.lang.Object;"))
