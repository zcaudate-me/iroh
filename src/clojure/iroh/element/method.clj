(ns iroh.element.method
  (:require [iroh.common :refer :all]
            [iroh.hierarchy :refer :all]
            [iroh.types.element :refer :all]
            [iroh.element.common :refer :all]
            [iroh.element.constructor]
            [iroh.pretty.classes :refer [class-convert]])
  (:import [java.lang.reflect Method]))

(defn invoke-static-method
  ([ele]
     (try (.invoke (:delegate ele) nil (object-array []))
          (catch IllegalArgumentException e
            (throw-arg-exception ele []))))
  ([ele args]
     (.invoke (:delegate ele) nil (object-array (box-args ele args)))))

(defn invoke-handle [^java.lang.invoke.MethodHandle handle args]
  (.invokeWithArguments handle (object-array args)))

(defn invoke-instance-method [ele args]
  (let [bargs (box-args ele args)]
    (if (-> ele :modifiers :abstract)
      (.invoke (:delegate ele) (first bargs) (object-array (rest bargs)))
      (invoke-handle (:handle ele) args))))

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
      (assoc :origins (origins obj))))

(defn to-pre-element [obj]
  (let [body (seed :method obj)
        body (if (:static body)
               (to-static-method obj body)
               (to-instance-method obj body))]
    body))

(def direct-method-handle-seed
  (to-pre-element
   (.getDeclaredMethod
    java.lang.invoke.DirectMethodHandle "make"
    (class-array Class [Class java.lang.invoke.MemberName]))))

(defn direct-method-handle [cls mn]
  (invoke-static-method direct-method-handle-seed [cls mn]))

(def direct-method-handle-make-seed
  (to-pre-element
   (.getDeclaredMethod
    java.lang.invoke.DirectMethodHandle "make"
    (class-array Class [Method]))))


(defn direct-method-handle-make [method]
  (invoke-static-method direct-method-handle-make-seed [method]))

(def method-type-seed
  (to-pre-element
   (.getDeclaredMethod
    java.lang.invoke.MethodType "makeImpl"
    (class-array Class [Class (Class/forName "[Ljava.lang.Class;") Boolean/TYPE]))))

(defn method-type [cls params type]
  (invoke-static-method method-type-seed [cls params type]))

(def member-name
  (to-element
   (.getDeclaredConstructor
    java.lang.invoke.MemberName (class-array Class [Method]))))

(defn direct-handle [ele]
  (let [mt (method-type (:type ele) (class-array Class (:params ele)) true)
        mn (member-name (:delegate ele))]
    ;;(direct-method-handle (:container ele) mn)
    (direct-method-handle-make (:delegate ele))))

(defmethod to-element java.lang.reflect.Method [obj]
  (let [body (-> (to-pre-element obj)
                 (assoc :type (.getReturnType obj)))
        body (if (-> body :modifiers :abstract)
               body
               (assoc body :handle (direct-handle body)))]
    (element body)))

(defmethod format-element :method [ele]
  (format-element-method ele))

(defmethod element-params :method [ele]
  (list (element-params-method ele)))
