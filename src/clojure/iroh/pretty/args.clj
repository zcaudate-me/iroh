(ns iroh.pretty.args
  (:require [iroh.common :refer :all]
            [iroh.pretty.class :refer [create-class]]))

(def sort-terms #{:by-name :by-params :by-modifiers :by-type})

(def display-terms #{:name :params :modifiers :type :attributes})

(defn classify-argument [arg]
  (cond (sort-terms arg)                 :sort-terms
        (display-terms arg)              :display-terms
        (number? arg)                    :num-args
        (or (= :# arg) (= :first arg))   :first
        (keyword? arg)                   :modifiers
        (or (string? arg) (regex? arg))  :name
        (or (set? arg) (vector? arg))    :params
        (hash-map? arg)                  :attributes
        (or (class? arg) (symbol? arg))  :type))

(defn convert-argument [arg]
  (condp = arg
    'boolean 'Boolean/TYPE
    'bool 'Boolean/TYPE
    'short 'Short/TYPE
    'int 'Integer/TYPE
    'long 'Long/TYPE
    'float 'Float/TYPE
    'double 'Double/TYPE
    'void   'Void/TYPE
    arg))

(defn convert-arguments [args]
  (mapv (fn [v]
          (if (vector? v)
            (mapv convert-argument v)
            (convert-argument v)))
        args))

(defn group-arguments [args]
  (-> (group-by classify-argument args)
      (update-in-if [:params] convert-arguments)
      (update-in-if [:type] convert-arguments)))

(defn has-name? [name value]
  (cond (regex? name)
        (not (nil? (re-find name value)))

        (string? name)
        (= name value)))

(defn has-params? [params value]
  (cond (set? params)
        (every? (set (map create-class params)) value)

        (vector? params)
        (= (mapv create-class params) value)))

(defn has-modifier? [modifier value]
  (contains? value modifier))

(defn has-type? [type value]
  (= (create-class type) value))

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

(sort (fn [x y]
        (compare (str x) (str y))) [Object Integer Class])

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

((comp inc #(* 2 %)) 1)

#_(let [dterms (sort (:display-terms grp))
        sterms (:sort-terms grp)
        get-first (:first grp)]
    (condp = (count dterms)
      0 (sort (sort-fn :name) eles)
      1 (distinct (sort (sort-fn str) (map (first dterms) eles)))
      (map #(select-keys (get % nil) dterms) eles))
    eles)
