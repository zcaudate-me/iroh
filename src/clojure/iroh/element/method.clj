(ns iroh.element.method
  (:require [iroh.types.element :refer [element
                                        invoke-element
                                        to-element
                                        format-element]]
            [iroh.element.common :refer [seed simple-name
                                         prepare-params]]))

(defmethod invoke-element :method
  ([ele]
     (if (:static ele)
       (.invoke (:delegate ele) [nil])
       (throw (Exception. "Cannot invoke non-static method element with no parameters"))))
  ([ele v & args]
     (if (:static ele)
       (.invoke (:delegate ele) (cons nil (cons v args)))
       (.invoke (:delegate ele) (cons v args)))))

(defmethod to-element java.lang.reflect.Method [obj]
  (let [body (seed :method obj)]
    (-> body
        (assoc :return-type (.getReturnType obj))
        (assoc :params (vec (seq (.getParameterTypes obj))))
        (element))))

(defmethod format-element :method [ele]
  (let [params (prepare-params ele)]
    (format "@(%s :: [%s] -> %s)"
                      (:name ele)
                      (clojure.string/join ", " params)
                      (simple-name (:return-type ele)))))

(comment
  (get (to-element (first (seq (.getDeclaredMethods java.lang.Object))))
       nil)

  (map to-element
       (seq (.getDeclaredMethods java.lang.Object)))
)
