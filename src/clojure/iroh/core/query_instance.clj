(ns iroh.core.query-instance
  (:require [iroh.hierarchy :as hierachy]
            [iroh.core.query-class :as q]
            [iroh.pretty.classes :as classes]
            [iroh.pretty.args :as args]
            [iroh.pretty.display :as display]))

(defn all-instance-elements
  "all-instance-elements"
  {:added "0.1.10"}
  [tcls icls]
  (let [supers (reverse (hierachy/inheritance-list tcls))
        eles   (mapcat #(q/list-class-elements % [:instance]) supers)]
    (concat eles
            (if icls (concat eles (q/list-class-elements icls [:static]))))))

(defn list-instance-elements
  "list-instance-elements"
  {:added "0.1.10"}
  [obj selectors]
  (let [grp (args/args-group selectors)
        tcls (type obj)]
    (->> (all-instance-elements tcls (if (class? obj) obj))
         (display/display grp))))

(defmacro .*
  ".*"
  {:added "0.1.10"}
  [obj & selectors]
  `(list-instance-elements ~obj ~(args/args-convert selectors)))
