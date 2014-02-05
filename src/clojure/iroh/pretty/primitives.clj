(ns iroh.pretty.primitives
  (:require [iroh.common :refer :all]))

(def primitive-reps #{:raw :symbol :string :class})

(def primitive-records
  [{:raw "Z" :symbol 'boolean :string "boolean" :class Boolean/TYPE}
   {:raw "B" :symbol 'byte    :string "byte"    :class Byte/TYPE}
   {:raw "C" :symbol 'char    :string "char"    :class Character/TYPE}
   {:raw "I" :symbol 'int     :string "int"     :class Integer/TYPE}
   {:raw "J" :symbol 'long    :string "long"    :class Long/TYPE}
   {:raw "F" :symbol 'float   :string "float"   :class Float/TYPE}
   {:raw "D" :symbol 'double  :string "double"  :class Double/TYPE}
   {:raw "V" :symbol 'void    :string "void"    :class Void/TYPE}])

(def primitive-combinations
  (let [combs (combinations 2 primitive-reps)]
    (concat combs (map (comp vec reverse) combs))))

(def primitive-lu
  (reduce (fn [lu [k1 k2 :as pair]]
            (assoc-in lu pair
                      (into {}
                            (map (fn [m] [(get m k1) (get m k2)]) primitive-records))))
          {} primitive-combinations))

(defn primitive-convert
  ([v to]
     (loop [[k & more] (seq (disj primitive-reps to))]
       (if-not (nil? k)
           (or (primitive-convert v k to)
               (recur more)))))
  ([v from to]
     (get-in primitive-lu [from to v])))
