(ns iroh.element.method
  (:require [iroh.types.element :refer [element invoke-element
                                        to-element format-element]]
            [iroh.element.common :refer [seed]]
            [iroh.pretty.classes :refer [class-convert]]))

(defmethod invoke-element :method
  ([ele]
     (if (:static ele)
       (.invoke (:delegate ele) nil (object-array []))
       (throw (Exception. "Cannot invoke non-static method element with no parameters"))))
  ([ele v & args]
     (if (:static ele)
       (.invoke (:delegate ele) nil (object-array (cons v args)))
       (.invoke (:delegate ele) v (object-array args)))))

(defmethod to-element java.lang.reflect.Method [obj]
  (let [ele (seed :method obj)
        ele (if (:static ele)
               (assoc ele :params (vec (seq (.getParameterTypes obj))))
               (assoc ele :params (vec (cons (:container ele) (seq (.getParameterTypes obj))))))]
      (-> ele
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
