(ns iroh.element.method
  (:require [iroh.common :refer :all]
            [iroh.hierarchy :refer :all]
            [iroh.types.element :refer :all]
            [iroh.element.common :refer [seed]]
            [iroh.pretty.classes :refer [class-convert]]))

(defmacro throw-arg-exception [ele args]
  `(throw (Exception. (format  "Method `%s` expects params to be of type %s, but was invoked with %s instead"
                              (str (:name ~ele))
                              (str (:params ~ele))
                              (str (mapv type ~args))))))

(defn box-args [ele args]
  (let [params (:params ele)]
    (if (= (count params) (count args))
      (try (mapv (fn [ptype arg]
                  (im.chit.iroh.Util/boxArg ptype arg))
                params
                args)
           (catch im.chit.iroh.BoxException e
             (throw-arg-exception ele args)))
      (throw-arg-exception ele args))))

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
      (assoc :origins (origins obj))))

(defmethod to-element java.lang.reflect.Method [obj]
  (let [body (seed :method obj)
        body (if (:static body)
               (to-static-method obj body)
               (to-instance-method obj body))]
      (-> body
          (assoc :type (.getReturnType obj))
          (element))))

(defn format-element-method [ele]
  (let [params (map #(class-convert % :string) (:params ele))]
    (format "#[%s :: (%s) -> %s]"
                      (:name ele)
                      (clojure.string/join ", " params)
                      (class-convert (:type ele) :string))))

(defmethod format-element :method [ele]
  (format-element-method ele))

(defn element-params-method [ele]
  (mapv #(symbol (class-convert % :string)) (:params ele)))

(defmethod element-params :method [ele]
  (element-params-method ele))
