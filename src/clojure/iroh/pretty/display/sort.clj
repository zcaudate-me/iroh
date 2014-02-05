(ns iroh.pretty.display.sort
  (:require [iroh.common :refer :all]))

(defn sort-fn [f]
  (fn [x y]
    (compare (f x) (f y))))

(defn sort-terms-fn [grp]
  (let [sterms (:sort-terms grp)]
    (fn [eles]
      (cond (nil? sterms) (sort (sort-fn :name) eles)
            :else eles))))
