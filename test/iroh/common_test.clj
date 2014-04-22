(ns iroh.common-test
  (:use midje.sweet)
  (:require [iroh.common :refer :all]))

^{:refer iroh.common/class-array :added "0.1.10"}
(fact "constructs a typed java array having the same length as `seq`"

  (class-array ["1" "2" "3"]) 
  => #(and (-> % type (.getName) (= "[Ljava.lang.String;"))
           (-> % count (= 3))) ;<java.lang.String[] ["1" "2" "3"]>
)

^{:refer iroh.common/context-class :added "0.1.10"}
(fact "If x is a class, return x otherwise return the class of x"
  
  (context-class String) 
  => String
  
  (context-class "")
  => String)

^{:refer iroh.common/assoc-if :added "0.1.10"}
(fact "`assoc` to the map only if the value is non-nil. Accepts multiple arguments."
  
  (assoc-if {} :a 1) 
  => {:a 1}
  
  (assoc-if {} :a nil) 
  => {})

^{:refer iroh.common/update-in-if :added "0.1.10"}
(fact "applies `f` to the nested map value only if it is non-nil."

  (update-in-if {:a {:b 1}} [:a :b] inc) 
  => {:a {:b 2}}
  
  (update-in-if {} [:a :b] inc) 
  => {})

^{:refer iroh.common/select-keys-nnil :added "0.1.10"}
(fact "select-keys that are non-nil"
  
  (select-keys-nnil {:a 1 :b 1}) 
  => {:a 1 :b 1}
  
  (select-keys-nnil {:a nil :b 1}) 
  => {:b 1})

^{:refer iroh.common/is-selected-key :added "0.1.10"}
(fact "is-selected-key")

^{:refer iroh.common/select-keys-fn :added "0.1.10"}
(fact "select-keys-fn")

^{:refer iroh.common/select-keys-nested :added "0.1.10"}
(fact "recursively walks a collection and applies select-keys to the map elements"

  (select-keys-nested {:a {:b 1}} [:b])
  => {}

  (select-keys-nested {:a {:b 1}} [:a :b])
  => {:a {:b 1}}

  (select-keys-nested {:a [{:b 1}]} [:a :b])
  => {:a [{:b 1}]})

^{:refer iroh.common/combinations :added "0.1.10"}
(fact "find all combinations of `k` in a given input list `l`"

  (combinations 2 [1 2 3])
  => [[2 1] [3 1] [3 2]]

  (combinations 3 [1 2 3 4])
  => [[3 2 1] [4 2 1] [4 3 1] [4 3 2]])

^{:refer iroh.common/all-subsets :added "0.1.10"}
(fact "finds all non-empty sets of collection `s`"

  (all-subsets [1 2 3])
  => [#{1} #{2} #{3} #{1 2} #{1 3} #{2 3} #{1 2 3}])

^{:refer iroh.common/conj-fn :added "0.1.10"}
(fact "constructs a function that takes a single argument `v` 
  and conj `x` to if. If `v` is empty, then construct it using `f`"
  
  ((conj-fn 1) nil) 
  => [1]
  
  ((conj-fn [:a 1]) {:b 2}) 
  => {:a 1 :b 2}
  
  ((conj-fn [:a 1] hash-map) nil) 
  => {:a 1})