(ns iroh.test-element-common
  (:use midje.sweet)
  (:require [iroh.element.common :refer :all]))

(fact "add-annotations"
  (add-annotations {} java.lang.Object)
  => {}

  (add-annotations {} java.lang.annotation.Documented)
  => {:annotations {java.lang.annotation.Documented "@java.lang.annotation.Documented()"
                    java.lang.annotation.Retention "@java.lang.annotation.Retention(value=RUNTIME)"
                    java.lang.annotation.Target "@java.lang.annotation.Target(value=[ANNOTATION_TYPE])"}}

  (add-annotations {} java.lang.Deprecated)
  => {:annotations {java.lang.annotation.Documented "@java.lang.annotation.Documented()"
                    java.lang.annotation.Retention "@java.lang.annotation.Retention(value=RUNTIME)"
                    java.lang.annotation.Target "@java.lang.annotation.Target(value=[CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE])"}})


(fact "seed"
  (seed :method (.getDeclaredMethod java.lang.Object "toString" (make-array Class 0)))
  => (just {:name "toString"
            :tag :method
            :hash number?
            :container java.lang.Object
            :modifiers #{:method :public}
            :static false
            :delegate #(instance? java.lang.reflect.Method %)})

  (seed :method (.getDeclaredConstructor java.lang.Object (make-array Class 0)))
  => (just {:name "java.lang.Object"
            :tag :method
            :hash number?
            :container java.lang.Object
            :modifiers #{:method :public}
            :static false
            :delegate #(instance? java.lang.reflect.Constructor %)})

  (seed :field (.getDeclaredField java.lang.Integer "TYPE"))
  => (just {:name "TYPE"
            :tag :field
            :hash number?
            :container java.lang.Integer
            :modifiers #{:static :public :final :field}
            :static true
            :delegate #(instance? java.lang.reflect.Field %)}))
