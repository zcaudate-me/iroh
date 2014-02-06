(ns iroh.element.method
  (:require [iroh.common :refer :all]
            [iroh.hierarchy :refer :all]
            [iroh.types.element :refer [element invoke-element
                                        to-element format-element]]
            [iroh.element.common :refer [seed]]
            [iroh.pretty.classes :refer [class-convert]]))

(defmethod invoke-element :method
  ([ele]
     (if (:static ele)
       (.invoke (:delegate ele) nil (object-array []))
       (throw (Exception. "Cannot invoke non-static method element with no parameters"))))
  ([ele v & args]
     (let [obj (:delegate ele)]
       (if (:static ele)
         (.invoke obj nil (box-args obj (object-array (cons v args))))
         (.invoke obj v   (box-args obj (object-array args)))))))

(defmethod to-element java.lang.reflect.Method [obj]
  (let [ele (seed :method obj)
        ele (if (:static ele)
              (-> ele
                  (assoc :params (vec (seq (.getParameterTypes obj))))
                  (assoc :origins (list (.getDeclaringClass obj))))
              (-> ele
                  (assoc :params (vec (cons (:container ele) (seq (.getParameterTypes obj)))))
                  (assoc :origins (origins obj))))]
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


(comment
  (use 'iroh.core)

  ((.? Long :method :private :#))
  )
