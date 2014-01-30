(ns iroh.element.constructor
  (:require [iroh.types.element :refer [element invoke-element
                                        to-element format-element]]
            [iroh.element.common :refer [seed prepare-params]]
            [iroh.pretty.class :refer [class-name]]))

(defmethod invoke-element :constructor [ele & args]
  (.newInstance (:delegate ele) (object-array args)))

(defmethod to-element java.lang.reflect.Constructor [obj]
  (let [body (seed :constructor obj)]
    (-> body
        (assoc :name "new")
        (assoc :static true)
        (assoc :type (.getDeclaringClass obj))
        (assoc :params (vec (seq (.getParameterTypes obj))))
        (element))))

(defmethod format-element :constructor [ele]
  (let [params (prepare-params ele)]
    (format "@(%s :: [%s] -> %s)"
                      (:name ele)
                      (clojure.string/join ", " params)
                      (class-name (:type ele)))))

(comment
  (get (to-element (first (seq (.getDeclaredConstructors java.lang.Object))))
       nil)

  (map to-element
       (seq (.getDeclaredConstructors java.lang.Object)))

  ((to-element (first (seq (.getDeclaredConstructors java.lang.Object)))))

  (.newInstance (:delegate ) (object-array []))
)
