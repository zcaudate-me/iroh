(ns iroh.core)

(def *static-desc* (atom {}))
(def *instance-desc* (atom {}))

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

(defn argument-type [p]
  (cond (sort-terms p)               :sort-terms
        (display-terms p)            :display-terms
        (keyword? p)                 :flags
        (or (string? p) (regex? p))  :name
        (or (set? p) (vector? p))    :params
        (hash-map? p)                :attributes
        (symbol? p)                  :return))

(defn convert-param [p]
  (condp = p
    'bool 'Boolean/TYPE
    'short 'Short/TYPE
    'int 'Integer/TYPE
    'long 'Long/TYPE
    'float 'Float/TYPE
    'double 'Double/TYPE
    'void   'Void/TYPE
    p))

(defn convert-params [ps]
  (map (fn [v]
         (if (vector? v)
           (mapv convert-param v)
           (convert-param v)))
       ps))

(->> [:private :static '[int String] 'int :name :by-name]
     (group-by param-type)
     (#(update-in % [:params] convert-params))
     (#(update-in % [:return] (comp convert-param last))))

(comment
  (defn group-by-search-terms [arr]
    )

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
