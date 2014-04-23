(ns iroh.pretty.display.sort
  (:require [iroh.common :refer :all]))

(defn sort-fn
  "returns a function taking two inputs `x` and `y`, comparing the outputs after applying `f` to both

  ((sort-fn :id) {:id 1} {:id 2}) => -1"
  {:added "0.1.10"}
  [f]
  (fn [x y]
    (compare (f x) (f y))))

(defn sort-terms-fn
  "This is a little broken, it is supposed to sort on various keys, but currently only works with :name

  ((sort-terms-fn {:sort-terms nil})
   [{:name 3}{:name 1} {:name 2}])
  => [{:name 1} {:name 2} {:name 3}]"
  {:added "0.1.10"}
  [grp]
  (let [sterms (:sort-terms grp)]
    (fn [eles]
      (cond (nil? sterms) (sort (sort-fn :name) eles)
            :else eles))))
