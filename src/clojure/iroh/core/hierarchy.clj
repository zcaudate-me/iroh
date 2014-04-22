(ns iroh.core.hierarchy
  (:require [iroh.common :as common]
            [iroh.element.common :as element]
            [iroh.hierarchy :as hierarchy]))

(defmacro .%
  ".%"
  {:added "0.1.10"}
  [obj]
  `(element/seed :class (common/context-class ~obj)))

(defmacro .%>
  ".%>"
  {:added "0.1.10"}
  [obj & args]
  `(let [t# (common/context-class ~obj)]
     (vec (concat [t#] (hierarchy/base-list t#)))))
