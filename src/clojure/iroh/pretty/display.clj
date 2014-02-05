(ns iroh.pretty.display
  (:require [iroh.common :refer :all]
            [iroh.pretty.args :refer [args-convert]]
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

(defn filter-elements [grp eles]
  (->> eles
       (filter-by has-name?     :name grp)
       (filter-by has-params?   :params grp)
       (filter-by has-num-args? :num-args grp :params)
       (filter-by has-type?     :type grp)
       (filter-by has-modifier? :modifiers grp)))

(defn sort-fn [f]
  (fn [x y]
    (compare (f x) (f y))))

(defn first-fn [grp]
  (if (:first grp) first identity))

(defn display-terms-fn [grp]
  (let [dterms (sort (:display-terms grp))]
    (fn [eles]
      (condp = (count dterms)
        0 eles
        1 (distinct (map (first dterms) eles))
        (map #(select-keys (get % nil) dterms) eles)))))

(defn sort-terms-fn [grp]
  (let [sterms (:sort-terms grp)]
    (fn [eles]
      (cond (nil? sterms) (sort (sort-fn :name) eles)
            :else eles))))

(defn display-elements [grp eles]
  ((comp
    (first-fn grp)
    (display-terms-fn grp)
    (sort-terms-fn grp)) eles))
