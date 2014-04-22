(ns iroh.pretty.args-test
  (:use midje.sweet)
  (:require [iroh.pretty.args :refer :all]))

^{:refer iroh.pretty.args/args-classify :added "0.1.10"}
(fact "args-classify"
  (map (fn [[i x]] [i (args-classify x)])
       {0  :by-name     ;; sort - :by-params, :by-modifiers, :by-type
        1  :tag         ;; display - :name, :params, :modifiers, :type, :attributes,
                        ;;           :origins, :container, :delegate
        2  :first       ;; gets the first element
        3  :#           ;; merge all elements into a single multi element
        4  "toString"   ;; matches exact name of function
        5  #"to*"       ;; matches name containing regex
        6  #(-> % :type (= :field))  ;; matches on predicate element
        7  #{Class}     ;; match origin of element
        8  [:any 'int]  ;; match any parameter type
        9  [:all 'int 'long] ;; match all parameter types
        10 ['byte 'byte] ;; match exact paramter types
        11 3             ;; match number of parameters
        13 'int          ;; match on the type of element
        14 :public       ;; match on modifiers (:public, :static, etc...)
        })
  => [[0 :sort-terms] [1 :select-terms] [2 :first] [3 :merge] [4 :name]
      [5 :name] [6 :predicate] [7 :origins] [8 :any-params] [9 :all-params]
      [10 :params] [11 :num-params] [13 :type] [14 :modifiers]])

^{:refer iroh.pretty.args/args-convert :added "0.1.10"}
(fact "args-convert")

^{:refer iroh.pretty.args/args-group :added "0.1.10"}
(fact "args-group")
