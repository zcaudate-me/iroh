(ns iroh.hierarchy
  (:require [iroh.common :as common]
            [iroh.element.common :as element]))

(defn interface?
  "Tests whether `x` is a java interface.

  (interface? java.util.Map)
  => true

  (interface? Class)
  => false"
  {:added "0.1.10"}
  [class]
  (.isInterface class))

(defn abstract?
  "Test whether `x` is abstract.

  (abstract? java.util.Map)
  => true

  (abstract? Class)
  => false"
  {:added "0.1.10"}
  [class]
  (java.lang.reflect.Modifier/isAbstract (.getModifiers class)))

(defn inheritance-list
  "Lists all direct superclasses of `cls`

  (inheritance-list String)
  => [java.lang.Object java.lang.String]

  (inheritance-list clojure.lang.PersistentHashMap)
  => [java.lang.Object clojure.lang.AFn
      clojure.lang.APersistentMap
      clojure.lang.PersistentHashMap]"
  {:added "0.1.10"}
  ([cls] (inheritance-list cls ()))
  ([cls output]
     (if (nil? cls)
       output
       (recur (.getSuperclass cls) (cons cls output)))))

(defn base-list
  "Lists all direct superclasses and interfaces of `cls`
  (base-list String)
  => [[java.lang.Object
       #{java.lang.CharSequence
         java.lang.Comparable
         java.io.Serializable}]]

  "
  {:added "0.1.10"}
  ([cls] (base-list cls []))
  ([cls output]
     (let [base (.getSuperclass cls)]
       (if-not base output
               (recur base
                      (conj output [base (-> (.getInterfaces cls) seq set)]))))))

(defn has-method
  "Checks to see if any given method exists in a particular class

  (has-method without-method
              String)
  => nil

  (has-method without-method
              clojure.lang.PersistentArrayMap)
  => clojure.lang.PersistentArrayMap"
  {:added "0.1.10"}
  [method class]
  (try (.getDeclaredMethod class
                           (.getName method) (.getParameterTypes method))
       class
       (catch NoSuchMethodException e)))

(defn methods-with-same-name-and-count
  "methods-with-same-name-and-count

  (methods-with-same-name-and-count without-method clojure.lang.IPersistentMap)
  =>  (#<Method clojure.lang.IPersistentMap.without(java.lang.Object)>)

  "
  {:added "0.1.10"}
  [method class]
  (let [methods (.getDeclaredMethods class)
        iname (.getName method)
        iparams (.getParameterTypes method)
        inargs (count iparams)
        smethods (filter (fn [x]
                           (and (= iname (.getName x))
                                (= inargs (count (.getParameterTypes x)))))
                         methods)]
    smethods))

(defn is-assignable?
  [bcls icls]
  (every? (fn [[b i]]
            (.isAssignableFrom b i))
          (map list bcls icls)))

(defn has-overridden-method
  "Checks to see that the method can be 

  (has-overridden-method without-method String)
  => nil

  (has-overridden-method without-method clojure.lang.IPersistentMap)
  => clojure.lang.IPersistentMap"
  {:added "0.1.10"}
  [method class]
  (let [smethods (methods-with-same-name-and-count method class)
        iparams (.getParameterTypes method)]
    (if (some (fn [smethod]
                (is-assignable?
                 (.getParameterTypes smethod)
                 iparams))
              smethods)
      class)))

(defn origins
  "Lists all the classes tha contain a particular method

  (def without-method
    (-> clojure.lang.PersistentArrayMap
        (.getDeclaredMethod \"without\"
                            (iroh.common/class-array [Object]))))

  (origins without-method)
  => [clojure.lang.IPersistentMap
      clojure.lang.PersistentArrayMap]"
  {:added "0.1.10"}
  ([method] (origins method (base-list (.getDeclaringClass method))))
  ([method bases] (origins method bases (list (.getDeclaringClass method))))
  ([method [[super interfaces :as pair] & more] currents]
     (if (nil? pair) currents
         (let [currents (if-let [current (has-overridden-method method super)]
                          (conj currents current)
                          currents)]
           (if-let [current (first (filter #(has-overridden-method method %) interfaces))]
             (conj currents current)
             (recur method more currents))))))
