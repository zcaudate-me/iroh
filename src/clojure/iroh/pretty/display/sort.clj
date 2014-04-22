(ns iroh.pretty.display.sort
  (:require [iroh.common :refer :all]))

(defn sort-fn
  "sort-fn"
  {:added "0.1.10"}
  [f]
  (fn [x y]
    (compare (f x) (f y))))

(defn sort-terms-fn
  "sort-terms-fn"
  {:added "0.1.10"}
  [grp]
  (let [sterms (:sort-terms grp)]
    (fn [eles]
      (cond (nil? sterms) (sort (sort-fn :name) eles)
            :else eles))))
