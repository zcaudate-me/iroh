(ns iroh.core.query-instance
  (:require [iroh.hierarchy :as hierachy]
            [iroh.core.query-class :as q]
            [iroh.pretty.classes :as classes]
            [iroh.pretty.args :as args]
            [iroh.pretty.display :as display]))

(defn all-instance-elements
  [tcls icls]
  (let [supers (reverse (hierachy/inheritance-list tcls))
        eles   (mapcat #(q/list-class-elements % [:instance]) supers)]
    (concat eles
            (if icls (concat eles (q/list-class-elements icls [:static]))))))

(defn list-instance-elements
  [obj selectors]
  (let [grp (args/args-group selectors)
        tcls (type obj)]
    (->> (all-instance-elements tcls (if (class? obj) obj))
         (display/display grp))))

(defmacro .*
  "lists what methods could be applied to a particular instance

  (.* \"abc\" :name #\"^to\")
  => [\"toCharArray\" \"toLowerCase\" \"toString\" \"toUpperCase\"]

  (.* String :name #\"^to\")
  => [\"toString\"]"
  {:added "0.1.10"}
  [obj & selectors]
  `(list-instance-elements ~obj ~(args/args-convert selectors)))
