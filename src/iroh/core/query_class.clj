(ns iroh.core.query-class
  (:require [iroh.common :as common]
            [iroh.types.element :as types]
            [iroh.element multi method field constructor]
            [iroh.pretty.args :as args]
            [iroh.pretty.display :as display])
  (:refer-clojure :exclude [.?]))

(defn- all-class-members
  [class]
  (concat
     (seq (.getDeclaredMethods class))
     (seq (.getDeclaredConstructors class))
     (seq (.getDeclaredFields class))))

(defn list-class-elements
  ([class]
     (->> (all-class-members class)
          (map types/to-element)))
  ([class selectors]
     (let [grp (args/args-group selectors)]
       (->> (list-class-elements class)
            (display/display grp)))))

(defmacro .?
  "queries the java view of the class declaration

  (.? String  #\"^c\" :name)
  => [\"charAt\" \"checkBounds\" \"codePointAt\" \"codePointBefore\"
      \"codePointCount\" \"compareTo\" \"compareToIgnoreCase\"
      \"concat\" \"contains\" \"contentEquals\" \"copyValueOf\"]"
  {:added "0.1.10"}
  [obj & selectors]
  `(list-class-elements (common/context-class ~obj) ~(args/args-convert selectors)))
