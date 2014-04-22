(ns iroh.test-package
  (:use midje.sweet)
  (:require [iroh.package :refer :all]))

(base-classloader)

(fact "jar-resource-path->class-name"

  (jar-resource-path->class-name "java/lang/String.class")
  => "java.lang.String"

  (jar-resource-path->class-name "java/lang/Class.class")
  => "java.lang.Class"

  (jar-resource-path->class-name "clojure/core$assoc.class")
  => "clojure.core$assoc")

(fact "class-name->jar-resource-path"
  (class-name->jar-resource-path "java.lang.String")
  => "java/lang/String.class"

  (class-name->jar-resource-path "clojure.core$assoc")
  => "clojure/core$assoc.class")

(fact "find-jar"
  (find-jar clojure.core$assoc)
  => [(str (System/getProperty "user.home")
           "/.m2/repository/org/clojure/clojure/1.5.1/clojure-1.5.1.jar")
      "clojure/core$assoc.class"]

  (find-jar String)
  => [(str (System/getProperty "java.home") "/lib/rt.jar")
      "java/lang/String.class"])

(fact "find-maven"
  (find-maven clojure.core$assoc)
  => '[org.clojure/clojure "1.5.1"]

  (find-maven String)
  => nil)

(-> (find-jar clojure.core$assoc)
    first
    jar->class-names
    sort
    (->> (take 2)))
