(ns iroh.pretty.display.filter
  (:require [clojure.set :as set]
            [iroh.common :refer :all]
            [iroh.pretty.classes :refer [class-convert]]))

(defn has-predicate?
  "has-predicate?"
  {:added "0.1.10"}
  [f value]
  (f value))

(defn has-name?
  "has-name?"
  {:added "0.1.10"}
  [name value]
  (cond (regex? name)
        (not (nil? (re-find name value)))

        (string? name)
        (= name value)))

(defn has-modifier?
  "has-modifier?"
  {:added "0.1.10"}
  [modifier value]
  (contains? value modifier))

(defn has-params?
  "has-params?"
  {:added "0.1.10"}
  [params value]
  (= (mapv class-convert params) value))

(defn has-num-params?
  "has-num-params?"
  {:added "0.1.10"}
  [num-params value]
  (= num-params (count value)))

(defn has-any-params?
  "has-any-params?"
  {:added "0.1.10"}
  [any-params value]
  (if (some #((set (map class-convert (next any-params))) %) value)
    true false))

(defn has-all-params?
  "has-all-params?"
  {:added "0.1.10"}
  [all-params value]
  (if (every? #((set value) %) (map class-convert (next all-params)))
    true false))

(defn has-type?
  "has-type?"
  {:added "0.1.10"}
  [type value]
  (= (class-convert type) value))

(defn has-origins?
  "has-origins?"
  {:added "0.1.10"}
  [origins value]
  (if (empty? (set/intersection origins (set value)))
    false true))

(defn filter-by
  "filter-by"
  {:added "0.1.10"}
  ([f k grp eles]
     (filter-by f k grp k eles))
  ([f kg grp ke eles]
     (if-let [chk (get grp kg)]
       (filter (fn [ele]
                 (every? #(f % (if ke (get ele ke) ele)) chk))
               eles)
       eles)))

(defn filter-terms-fn
  "filter-terms-fn"
  {:added "0.1.10"}
  [grp]
  (fn [eles]
    (->> eles
         (filter-by has-name?       :name grp)
         (filter-by has-predicate?  :predicate grp nil)
         (filter-by has-origins?    :origins grp)
         (filter-by has-type?       :type grp)
         (filter-by has-params?     :params grp)
         (filter-by has-any-params? :any-params grp :params)
         (filter-by has-any-params? :all-params grp :params)
         (filter-by has-num-params? :num-params grp :params)
         (filter-by has-modifier?   :modifiers grp))))
