(ns iroh.common
  (:require [hara.namespace.import :as im]
            [hara.common])
  (:import im.chit.iroh.Util))

(def ^:dynamic *cache* (atom {}))
  
(im/import
  hara.common [suppress hash-map? regex?])

(defn class-array
  "constructs a typed java array having the same length as `seq`

  (class-array [\"1\" \"2\" \"3\"]) 
  => <java.lang.String[] [\"1\" \"2\" \"3\"]>"
  {:added "0.1.10"}
  ([seq] (class-array (-> seq first type) seq))
  ([type seq]
    (let [total (count seq)
        arr   (make-array type total)]
    (doseq [i (range total)]
      (aset arr i (nth seq i)))
    arr)))

(defn context-class
  "If x is a class, return x otherwise return the class of x
  
  (context-class String) 
  => String
  
  (context-class \"\")
  => String"
  {:added "0.1.10"}
  [obj]
  (if (class? obj) obj (type obj)))

(defn assoc-if
  "`assoc` to the map only if the value is non-nil. Accepts multiple arguments.
  
  (assoc-if {} :a 1) 
  => {:a 1}
  
  (assoc-if {} :a nil) 
  => {}"
  {:added "0.1.10"}
  ([m k v] (assoc-if m k v identity))
  ([m k v f]
     (if v
       (assoc m k (f v))
       m)))

(defn update-in-if
  "applies `f` to the nested map value only if it is non-nil.

  (update-in-if {:a {:b 1}} [:a :b] inc) 
  => {:a {:b 2}}
  
  (update-in-if {} [:a :b] inc) 
  => {}"
  {:added "0.1.10"}
  ([m ks f]
     (if-let [v (get-in m ks)]
       (assoc-in m ks (f v))
       m)))

(defn select-keys-nnil
  "select-keys that are non-nil
  
  (select-keys-nnil {:a 1 :b 1}) 
  => {:a 1 :b 1}
  
  (select-keys-nnil {:a nil :b 1}) 
  => {:b 1}"
  {:added "0.1.10"}
  [m]
  (into {}
        (filter (fn [[k v]]
                  (not (nil? v)))
                m)))

(defn- is-selected-key
  "is-selected-key"
  {:added "0.1.10"}
  [vs]
  (fn [p]
    (some (fn [v]
            (or (= v p)
                (v p)))
          vs)))

(defn- select-keys-fn
  "select-keys-fn"
  {:added "0.1.10"}
  [coll ks]
  (let [pks (keys coll)
        pks (filter (is-selected-key ks) pks)]
    (select-keys coll pks)))

(defn select-keys-nested
  "recursively walks a collection and applies select-keys to the map elements

  (select-keys-nested {:a {:b 1}} [:b])
  => {}

  (select-keys-nested {:a {:b 1}} [:a :b])
  => {:a {:b 1}}

  (select-keys-nested {:a [{:b 1}]} [:a :b])
  => {:a [{:b 1}]}"
  {:added "0.1.10"}
  [coll ks]
  (cond (hash-map? coll)
        (-> (->> coll
                 (map (fn [[k v]]
                        [k (select-keys-nested v ks)]))
                 (into (empty coll)))
            (select-keys-fn ks))

        (or (list? coll)
            (vector? coll)
            (set? coll))
        (->> coll
             (map #(select-keys-nested % ks))
             (into (empty coll)))

        :else
        coll))

(defn combinations
  "find all combinations of `k` in a given input list `l`

  (combinations 2 [1 2 3])
  => [[2 1] [3 1] [3 2]]

  (combinations 3 [1 2 3 4])
  => [[3 2 1] [4 2 1] [4 3 1] [4 3 2]]"
  {:added "0.1.10"}
  [k l]
  (if (= 1 k) (map vector l)
      (apply concat
             (map-indexed
              #(map (fn [x] (conj x %2))
                    (combinations (dec k) (drop (inc %1) l)))
              l))))

(defn all-subsets
  "finds all non-empty sets of collection `s`

  (all-subsets [1 2 3])
  => [#{1} #{2} #{3} #{1 2} #{1 3} #{2 3} #{1 2 3}]"
  {:added "0.1.10"}
  [s]
  (apply concat
         (for [x (range 1 (inc (count s)))]
           (map #(into #{} %) (combinations x s)))))

(defn conj-fn
  "constructs a function that takes a single argument `v` 
  and conj `x` to if. If `v` is empty, then construct it using `f`
  
  ((conj-fn 1) nil) 
  => [1]
  
  ((conj-fn [:a 1]) {:b 2}) 
  => {:a 1 :b 2}
  
  ((conj-fn [:a 1] hash-map) nil) 
  => {:a 1}"
  {:added "0.1.10"}
  ([x] (conj-fn x vector))
  ([x f]
      (fn [v]
        (if-not v (conj (f) x) (conj v x)))))

