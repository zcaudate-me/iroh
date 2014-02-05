(ns iroh.pretty.display.filter
  (:require [iroh.common :refer :all]
            [iroh.pretty.classes :refer [class-convert]]))

(defn has-name? [name value]
  (cond (regex? name)
        (not (nil? (re-find name value)))

        (string? name)
        (= name value)))

(defn has-params? [params value]
  (cond (set? params)
        (every? (set (map class-convert params)) value)

        (vector? params)
        (= (mapv class-convert params) value)))

(defn has-modifier? [modifier value]
  (contains? value modifier))

(defn has-type? [type value]
  (= (class-convert type) value))

(defn has-num-args? [num-args value]
  (= num-args (count value)))

(defn filter-by
  ([f k grp eles]
     (filter-by f k grp k eles))
  ([f kg grp ke eles]
     (if-let [chk (get grp kg)]
       (filter (fn [ele]
                 (every? #(f % (get ele ke)) chk))
               eles)
       eles)))

(defn filter-terms-fn [grp]
  (fn [eles]
    (->> eles
         (filter-by has-name?     :name grp)
         (filter-by has-params?   :params grp)
         (filter-by has-num-args? :num-args grp :params)
         (filter-by has-type?     :type grp)
         (filter-by has-modifier? :modifiers grp))))