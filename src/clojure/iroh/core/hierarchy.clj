(ns iroh.core.hierarchy
  (:require [iroh.common :as common]
            [iroh.element.common :as element]
            [iroh.hierarchy :as hierarchy]))

(defmacro .%
  "Lists class information

  (.% String)  ;; or (.%> \"\")
  => (contains {:name \"java.lang.String\"
                :tag :class
                :hash anything
                :container nil
                :modifiers #{:instance :class :public :final}
                :static false
                :delegate java.lang.String})"
  {:added "0.1.10"}
  [obj]
  `(element/seed :class (common/context-class ~obj)))

(defmacro .%>
  "Lists the class and interface hierarchy for the class

  (.%> String)   ;; or (.%> \"\")
  => [java.lang.String
      [java.lang.Object
       #{java.io.Serializable
         java.lang.Comparable
         java.lang.CharSequence}]]"
  {:added "0.1.10"}
  [obj & args]
  `(let [t# (common/context-class ~obj)]
     (vec (concat [t#] (hierarchy/base-list t#)))))
