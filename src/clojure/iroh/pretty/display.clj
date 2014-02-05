(ns iroh.pretty.display
  (:require [iroh.common :refer :all]
            [iroh.pretty.display.filter :refer [filter-terms-fn]]
            [iroh.pretty.display.sort :refer [sort-terms-fn]]))

(defn first-terms-fn [grp]
  (if (:first grp) first identity))

(defn select-terms-fn [grp]
  (let [sterms (sort (:select-terms grp))]
    (fn [eles]
      (condp = (count sterms)
        0 eles
        1 (distinct (map (first sterms) eles))
        (map #(select-keys (get % nil) sterms) eles)))))

(defn display [grp eles]
  ((comp
    (first-terms-fn grp)
    (select-terms-fn grp)
    (sort-terms-fn grp)
    (filter-terms-fn grp)) 
    eles))
