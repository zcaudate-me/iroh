(ns iroh.core.delegate
  (:require [iroh.core.query-instance :as q]))

(deftype Delegate [pointer fields]
  Object
  (toString [self]
    (format "<%s@%s %s>" (.getName (type pointer)) (.hashCode pointer) (self)))

  clojure.lang.IDeref
  (deref [self]
    (self))

  java.util.Map
  (equals [self other] (= (self) other))
  (size [self] (count fields))
  (keySet [self] (keys fields))
  (entrySet [self] (set (map (fn [[k f]] (clojure.lang.MapEntry. k (f pointer))) fields)))
  (containsKey [self key] (contains? fields key))
  (values [self] (map (fn [f] (f pointer)) (vals fields)))

  clojure.lang.ILookup
  (valAt [self key]
    (if-let [f (get fields key)]
      (f pointer)))
  (valAt [self key not-found]
    (if-let [f (get fields key)]
      (f pointer)
      not-found))

  clojure.lang.IFn
  (invoke [self]
    (->> fields
         (map (fn [[k f]]
                [k (f pointer)]))
         (into {})))
  (invoke [self key]
    (.valAt self key))
  (invoke [self key value]
    (if-let [f (get fields key)]
      (f pointer value))
    self))

(defn delegate
  "delegate"
  {:added "0.1.10"}
  [obj]
    (let [fields (->> (map (juxt (comp keyword :name) identity) (q/.* obj :field))
                      (into {}))]
      (Delegate. obj fields)))

(defmethod print-method Delegate
  [v w] (.write w (str v)))
