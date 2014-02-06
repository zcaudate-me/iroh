(ns iroh.hierarchy
  (:require [iroh.common :refer :all]))

(defn interface? [class]
  (.isInterface class))

(defn abstract? [class]
  (java.lang.reflect.Modifier/isAbstract (.getModifiers class)))

(defn base-list
  ([cls] (base-list cls []))
  ([cls output]
     (let [base (.getSuperclass cls)]
       (if-not base output
               (recur base
                      (conj output [base (-> (.getInterfaces cls) seq set)]))))))

(defn has-method [method class]
  (try (.getDeclaredMethod class
                           (.getName method) (.getParameterTypes method))
       class
       (catch NoSuchMethodException e)))

(defn origins
  ([method] (origins method (base-list (.getDeclaringClass method))))
  ([method bases] (origins method bases (list (.getDeclaringClass method))))
  ([method [[super interfaces :as pair] & more] currents]
     (if (nil? pair) currents
         (let [currents (if-let [current (has-method method super)]
                          (conj currents current)
                          currents)]
           (if-let [current (first (map #(has-method method %) interfaces))]
             (conj currents current)
             (recur method more currents))))))
