(ns iroh.core
  (:require [iroh.common :refer :all]
            [iroh.types.element :refer [to-element]]
            [iroh.pretty.args :refer [group-arguments filter-elements]]
            [iroh.element.method]
            [iroh.element.field]
            [iroh.element.constructor]))

(def ^:dynamic *static-description* (atom {}))
(def ^:dynamic *instance-description* (atom {}))



(defn select-elements [class selectors]
  (filter-elements
   (group-arguments selectors)
   (map to-element
        (concat
         (seq (.getDeclaredMethods class))
         (seq (.getDeclaredConstructors class))
         (seq (.getDeclaredFields class))))))

(defn dot-star [obj & selectors])

(defmacro .* [obj & selectors]
  `(dot-star ~obj ~@selectors))

(defn dot-question [class & selectors]
  (select-elements class selectors))

(defmacro .? [class & selectors]
  `(dot-question ~class ~@selectors))

(defn dot-question> [class & selectors]
  (let [eles (select-elements class selectors)
        names (map :name eles)]
    (if (apply = names)
      (first eles)
      (throw (Exception. (str "There are multiple named methods"
                              (-> names distinct sort)))))))

(defmacro .?> [class & selectors]
  `(dot-question> ~class ~@selectors))

(defn dot-dollar [obj method & args])

(defmacro .$ [obj method & args]
  `(dot-dollar ~obj ~method ~@args))

(defmacro .$> [obj & forms])

(comment

  (.% Object parseInt)

  (>refresh)
  (def f (-> (.? Integer :public #"parse")
             first))

  (Integer/parseInt)

  (f "100")

  (Class/forName "[[[Ljava.lang.Object;")

  (.* 1)

  (-> (.?> Integer :private :field :static)
      (get nil))

  ((.# Integer :private) (int -1))

  (.# Object :private) ;; => lists all the private variables

  (.# Object :private :static [int String]) ;; => lists all the private static members of return type int String

  (.# Object :private :static :field)

  (.# Object :private :static :method)

  (.# Object :constructor)

  (.# Object "toString")

  (.# Object :private #"get")

  (.# Object :static #"get")

)
