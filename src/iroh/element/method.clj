(ns iroh.element.method
  (:require [iroh.common :refer :all]
            [iroh.hierarchy :as hierachy]
            [iroh.types.element :refer :all]
            [iroh.element.common :refer :all]
            [iroh.pretty.classes :refer [class-convert]]))

(defn invoke-static-method
  ([ele]
     (try (.invoke (:delegate ele) nil (object-array []))
          (catch IllegalArgumentException e
            (throw-arg-exception ele []))))
  ([ele args]
     (.invoke (:delegate ele) nil (object-array (box-args ele args)))))

(defn invoke-instance-method [ele args]
  (let [bargs (box-args ele args)]
    (.invoke (:delegate ele) (first bargs) (object-array (rest bargs)))))

(defmethod invoke-element :method
  ([ele]
     (if (:static ele)
       (invoke-static-method ele)
       (throw-arg-exception ele [])))
  ([ele & args]
     (if (:static ele)
       (invoke-static-method ele args)
       (invoke-instance-method ele args))))

(defn to-static-method [obj body]
  (-> body
      (assoc :params (vec (seq (.getParameterTypes obj))))
      (assoc :origins (list (.getDeclaringClass obj)))))

(defn to-instance-method [obj body]
  (-> body
      (assoc :params (vec (cons (:container body) (seq (.getParameterTypes obj)))))
      (assoc :origins (hierachy/origins obj))))

(defn to-pre-element [obj]
  (let [body (seed :method obj)
        body (if (:static body)
               (to-static-method obj body)
               (to-instance-method obj body))]
    body))

(defmethod to-element java.lang.reflect.Method [obj]
  (let [body (-> (to-pre-element obj)
                 (assoc :type (.getReturnType obj)))]
    (element body)))

(defmethod format-element :method [ele]
  (format-element-method ele))

(defmethod element-params :method [ele]
  (list (element-params-method ele)))
