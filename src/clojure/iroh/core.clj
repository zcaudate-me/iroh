(ns iroh.core
  (:require [iroh.common :refer :all]
            [iroh.types.element :refer [to-element]]
            [iroh.element.method]
            [iroh.element.field]
            [iroh.element.constructor]))

(def ^:dynamic *static-description* (atom {}))
(def ^:dynamic *instance-description* (atom {}))

(defn dot-star [obj & selectors])

(defmacro .* [obj & selectors]
  `(dot-star ~obj ~@selectors))

(defn dot-hash [class & selectors])

(defmacro .# [class & selectors]
  `(dot-hash ~class ~@selectors))

(defn dot-dollar [obj method & args])

(defmacro .$ [obj method & args]
  `(dot-dollar ~obj ~method ~@args))

(defmacro .$> [obj & forms])

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
        (every? params value)

        (vector? params)
        (= params value)))

(defn has-modifier? [modifier value]
  (contains? value modifier))

(defn has-type? [type value]
  (= type value))

(defn filter-elements [grp eles]
  (->> eles
       (filter-by has-name?   :name grp)
       (filter-by has-params? :params grp)
       (filter-by has-type?   :type grp)
       (filter-by has-modifier? :modifiers grp)))

(comment
  (group-arguments [:private :static '[int String] 'int :name :by-name])
  => {:flags [:private :static]
      :params '[[Integer/TYPE String]]
      :return 'Integer/TYPE
      :display-terms [:name]
      :sort-terms [:by-name]}

  (defn group-by-search-terms [arr]
    )

  (filter-elements
   (group-arguments ["toString"])
   (map to-element
        (seq (.getDeclaredMethods java.lang.Object))))

  (filter-elements
   (group-arguments [#"get"])
   (map to-element
        (seq (.getDeclaredMethods java.lang.Object))))

  (filter-elements
   (group-arguments [:private])
   (map to-element
        (seq (.getDeclaredMethods java.lang.Object))))

  (filter-elements
   (group-arguments [:private :static])
   (map to-element
        (seq (.getDeclaredMethods java.lang.Class))))

  (Class/forName "[[[Ljava.lang.Object;")
  (.* 1)

  (.# Object :private) ;; => lists all the private variables

  (.# Object :private :static [int String]) ;; => lists all the private static members of return type int String

  (.# Object :private :static :field)

  (.# Object :private :static :method)

  (.# Object :constructor)

  (.# Object "toString")

  (.# Object :private #"get")

  (.# Object :static #"get")

)
