(ns iroh.element.multi
  (:require [iroh.types.element :refer [element invoke-element to-element]]))

(defmethod invoke-element :multi [obj & args])

(defn get-name [v]
  (let [names (map :name v)]
    (assert (and (apply = names)
                 (first names))
            "All elements in vector must have the same name")
    (first names)))

(defn create-lookup [eles]
  (reduce
   (fn [m ele]
     (let [params (:params ele)]
       (assoc-in m [(count params) params]))) {} eles))

(defmethod to-element clojure.lang.PersistentVector [v]
  (assert (or (apply = :method      (map :tag v))
              (apply = :constructor (map :tag v)))
          "All elements must be methods")
  (let [body {:name (get-name v)
              :lookup (create-lookup v)}]
    (element :multi body)))
