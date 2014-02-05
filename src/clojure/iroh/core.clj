(ns iroh.core
  (:require [iroh.common :refer :all]
            [iroh.types.element :refer [to-element]]
            [iroh.pretty.args :refer [args-convert args-group]]
            [iroh.pretty.display :refer [display]]
            [iroh.element.method]
            [iroh.element.field]
            [iroh.element.constructor]
            ))

(def ^:dynamic *static-description* (atom {}))
(def ^:dynamic *instance-description* (atom {}))

(defn select-elements [class selectors]
  (let [grp (args-group selectors)]
    (->> (concat
               (seq (.getDeclaredMethods class))
               (seq (.getDeclaredConstructors class))
               (seq (.getDeclaredFields class)))
         (map to-element)
         (display grp))))

(defn dot-star [obj & selectors])

(defmacro .* [obj & selectors]
  `(dot-star ~obj ~@selectors))

(defn dot-question [class & selectors]
  (select-elements class selectors))

(defmacro .? [class & selectors]
  `(dot-question ~class ~@(args-convert selectors)))

(defn dot-dollar [obj method & args])

(defmacro .$ [obj method & args]
  `(dot-dollar ~obj ~method ~@args))
