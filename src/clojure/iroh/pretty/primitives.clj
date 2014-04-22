(ns iroh.pretty.primitives
  (:require [iroh.common :refer :all]))

(def primitive-reps #{:raw :symbol :string :class :container})

(def primitive-records
  [{:raw "Z" :symbol 'boolean :string "boolean" :class Boolean/TYPE   :container Boolean}
   {:raw "B" :symbol 'byte    :string "byte"    :class Byte/TYPE      :container Byte}
   {:raw "C" :symbol 'char    :string "char"    :class Character/TYPE :container Character}
   {:raw "I" :symbol 'int     :string "int"     :class Integer/TYPE   :container Integer}
   {:raw "J" :symbol 'long    :string "long"    :class Long/TYPE      :container Long}
   {:raw "F" :symbol 'float   :string "float"   :class Float/TYPE     :container Float}
   {:raw "D" :symbol 'double  :string "double"  :class Double/TYPE    :container Double}
   {:raw "V" :symbol 'void    :string "void"    :class Void/TYPE      :container Void}])

(def primitive-classes (set (map :class primitive-records)))

(def primitive-containers (set (map :container primitive-records)))

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
  "Converts primitive values across their different representations. The choices are:
   :raw       - The string in the jdk (i.e. `Z` for Boolean, `C` for Character)
   :symbol    - The symbol that iroh uses for matching (i.e. boolean, char, int)
   :string    - The string that iroh uses for matching
   :class     - The primitive class representation of the primitive
   :container - The containing class representation for the primitive type

  (primitive-convert Boolean/TYPE :symbol)
  => 'boolean

  (primitive-convert \"Z\" :symbol)
  => 'boolean

  (primitive-convert \"int\" :symbol)
  => 'int

  (primitive-convert Character :string)
  => \"char\"

  (primitive-convert \"V\" :class)
  => Void/TYPE

  (primitive-convert 'long :container)
  => Long"
  {:added "0.1.10"}
  ([v to]
     (loop [[k & more] (seq (disj primitive-reps to))]
       (if-not (nil? k)
         (or (primitive-convert v k to)
             (recur more)))))
  ([v from to]
     (get-in primitive-lu [from to v])))
