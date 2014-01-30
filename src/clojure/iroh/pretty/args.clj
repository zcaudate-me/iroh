(ns iroh.pretty.args
  (:require [iroh.common :refer :all]
            [iroh.pretty.class :refer [create-class]]))

(def sort-terms #{:by-name :by-params :by-flags :by-return-type})

(def display-terms #{:name :params :flags :return-type :attributes})

(defn classify-argument [arg]
  (cond (sort-terms arg)                 :sort-terms
        (display-terms arg)              :display-terms
        (keyword? arg)                   :modifiers
        (or (string? arg) (regex? arg))  :name
        (or (set? arg) (vector? arg))    :params
        (hash-map? arg)                  :attributes
        (symbol? arg)                    :type))

(defn convert-argument [arg]
  (condp = arg
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
      (update-in [:params] convert-arguments)
      (update-in [:type] convert-arguments)))

(defn has-name? [name value]
  (cond (regex? name)
        (re-find name value)

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
  (= type value))

(defn filter-by [f k grp eles]
  (if-let [chk (get grp k)]
    (filter (fn [ele]
              (every? #(f % (get ele k)) chk))
            eles)
    eles))

(defn filter-elements [grp eles]
  (->> eles
       (filter-by has-name?   :name grp)
       (filter-by has-params? :params grp)
       (filter-by has-type?   :type grp)
     (filter-by has-modifier? :modifiers grp)))
