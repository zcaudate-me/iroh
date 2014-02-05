(ns iroh.element.field
  (:require [iroh.types.element :refer [element invoke-element
                                        to-element format-element]]
            [iroh.element.common :refer [seed]]
            [iroh.pretty.classes :refer [class-convert]]))

(def patch-field
  (let [mf (.getDeclaredField java.lang.reflect.Field  "modifiers")]
    (.setAccessible mf true)
    (fn [field]
      (.setInt mf field (bit-and (.getModifiers field) (bit-not java.lang.reflect.Modifier/FINAL)))
      field)))

(defmethod invoke-element :field
  ([ele]
     (if (:static ele)
       (.get (:delegate ele) [nil])
       (throw (Exception. "Cannot invoke field element with no parameters"))))
  ([ele x]
     (if (:static ele)
       (do (.set (:delegate ele) nil x) ele)
       (.get (:delegate ele) x)))

  ([ele x y]
     (if (:static ele)
       (throw (Exception. "Cannot invoke static field element with two parameters"))
       (do (.set (:delegate ele) x y)
           x))))

(defmethod to-element java.lang.reflect.Field [obj]
  (let [body (seed :field obj)]
    (-> body
        (assoc :type (.getType obj))
        (assoc :delegate (patch-field obj))
        (element))))

(defmethod format-element :field [ele]
  (let [params (map #(class-convert % :string) (:params ele))]
    (if (:static ele)
      (format "#[%s :: %s]"
              (:name ele)
              (class-convert (:type ele) :string))
      (format "#[%s :: [%s] | %s]"
              (:name ele)
              (clojure.string/join ", " params)
              (class-convert (:type ele) :string)))))
