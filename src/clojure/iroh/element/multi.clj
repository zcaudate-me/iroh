(ns iroh.element.multi
  (:require [iroh.types.element :refer [element invoke-element to-element]]))

(defmethod invoke-element :multi [obj & args])

(defmethod to-element clojure.lang.PersistentVector [obj]
  (let [body {}]
    (element :multi body)))
