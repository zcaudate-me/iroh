(ns iroh.test-common
  (:use midje.sweet)
  (:require [iroh.common :refer :all]))

(fact "hash-map?"
  (hash-map? {}) => true
  (hash-map? []) => false)


(fact "regex?"
  (regex? #"") => true
  (regex? []) => false)

(fact "assoc-if"
  (assoc-if {} :a 1)  => {:a 1}
  (assoc-if {} :a nil) => {})

(fact "select-keys-nnil"
  (select-keys-nnil {:a 1 :b 1}) => {:a 1 :b 1}
  (select-keys-nnil {:a nil :b 1}) => {:b 1})


(fact "select-keys-nested"
  (select-keys-nested {:a 1} [:a])
  => {:a 1}

  (select-keys-nested {:a 1} [:b])
  => {}

  (select-keys-nested {:a {:b 1}} [:b])
  => {}

  (select-keys-nested {:a {:b 1}} [:a :b])
  => {:a {:b 1}}

  (select-keys-nested {:a [{:b 1}]} [:a :b])
  => {:a [{:b 1}]})

(fact "combinations"
  (combinations 2 [1 2 3])
  => [[2 1] [3 1] [3 2]]

  (combinations 3 [1 2 3 4])
  => [[3 2 1] [4 2 1] [4 3 1] [4 3 2]])


(fact "all-subsets"
  (all-subsets [1 2 3])
  => [#{1} #{2} #{3} #{1 2} #{1 3} #{2 3} #{1 2 3}])
