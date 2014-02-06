(defmethod invoke-element :constructor [ele & args]
  (let [obj (:delegate ele)]
    (.newInstance obj (box-args obj (object-array args)))))

(defmethod to-element java.lang.reflect.Constructor [obj]
  (let [body (seed :constructor obj)]
    (-> body
        (assoc :name "new")
        (assoc :static true)
        (assoc :type (.getDeclaringClass obj))
        (assoc :params (vec (seq (.getParameterTypes obj))))
        (element))))

(defmethod format-element :constructor [ele]
  (format-element-method ele))
