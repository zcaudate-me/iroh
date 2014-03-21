(ns iroh.element.field
  (:require [iroh.common :refer [context-class]]
            [iroh.types.element :refer :all]
            [iroh.element.common :refer [seed]]
            [iroh.pretty.classes :refer [class-convert]])
  (:import im.chit.iroh.Util))

(def patch-field
  (let [mf (.getDeclaredField java.lang.reflect.Field  "modifiers")]
    (.setAccessible mf true)
    (fn [field]
      (.setInt mf field (bit-and (.getModifiers field) (bit-not java.lang.reflect.Modifier/FINAL)))
      field)))

(defn arg-params [ele access]
  (let [args [(:container ele)]]
    (condp = access
      :set (conj args (:type ele))
      :get args)))

(defmacro throw-arg-exception [ele args]
  `(throw (Exception. (format  "Accessor `%s` expects %s for getter or %s for setter, but was invoked with %s."
                              (str (:name ~ele))
                              (arg-params ~ele :get)
                              (arg-params ~ele :set)
                              (mapv #(symbol (class-convert
                                              (context-class %) :string))
                                    ~args)))))

(defn invoke-static-field
  ([ele cls]
     (.get (:delegate ele) nil))
  ([ele cls val]
     (Util/setField (:delegate ele) nil val)
     true))

(defn invoke-instance-field
  ([ele obj]
     (.get (:delegate ele) (Util/boxArg (:container ele) obj)))
  ([ele obj val]
     (Util/setField (:delegate ele) (Util/boxArg (:container ele) obj) val)
     true))

(defmethod invoke-element :field
  ([ele]
     (throw-arg-exception ele []))
  ([ele x]
     (if (:static ele)
       (invoke-static-field ele x)
       (invoke-instance-field ele x)))

  ([ele x y]
     (if (:static ele)
       (invoke-static-field ele x y)
       (invoke-instance-field ele x y)))
  ([ele x y & more]
     (throw-arg-exception ele (vec (concat [x y] more)))))

(defmethod to-element java.lang.reflect.Field [obj]
  (let [body (seed :field obj)
        type (.getType obj)]
    (-> body
        (assoc :type type)
        (assoc :origins (list (:container body)))
        (assoc :params (if (:static body) [] [(:container body)]))
        (assoc :delegate (patch-field obj))
        (element))))

(defmethod format-element :field [ele]
  (if (:static ele)
    (format "#[%s :: <%s> | %s]"
            (:name ele)
            (.getName (:container ele))
            (class-convert (:type ele) :string))
    (format "#[%s :: (%s) | %s]"
            (:name ele)
            (.getName (:container ele))
            (class-convert (:type ele) :string))))

(defmethod element-params :field [ele]
  (list (mapv #(symbol (class-convert % :string)) (arg-params ele :get))
        (mapv #(symbol (class-convert % :string)) (arg-params ele :set))))
