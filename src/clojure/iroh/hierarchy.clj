(ns iroh.hierarchy
  (:require [iroh.common :refer :all]))

(defn interface? [class]
  (.isInterface class))

(defn abstract? [class]
  (java.lang.reflect.Modifier/isAbstract (.getModifiers class)))

(defn inheritance-list
  ([cls] (inheritance-list cls ()))
  ([cls output]
     (if (nil? cls)
       output
       (recur (.getSuperclass cls) (cons cls output)))))

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

(defn methods-with-same-name-and-count [method class]
  (let [methods (.getDeclaredMethods class)
        iname (.getName method)
        iparams (.getParameterTypes method)
        inargs (count iparams)
        smethods (filter (fn [x]
                           (and (= iname (.getName x))
                                (= inargs (count (.getParameterTypes x)))))
                         methods)]
    smethods))

(defn is-assignable? [bcls icls]
  (every? (fn [[b i]]
            (.isAssignableFrom b i))
          (map list bcls icls)))

(defn has-overridden-method [method class]
  (let [smethods (methods-with-same-name-and-count method class)
        iparams (.getParameterTypes method)]
    (if (some (fn [smethod]
                (is-assignable?
                 (.getParameterTypes smethod)
                 iparams))
              smethods)
      class)))

(defn origins
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



(comment
  (def without-method (.getDeclaredMethod clojure.lang.PersistentArrayMap "without"
                                          (class-array Class [Object])))

  (origins without-method)
  (origins (.getDeclaredMethod String "charAt"
                               (class-array Class [Integer/TYPE])))

  (methods-with-same-name-and-count without-method clojure.lang.IPersistentMap)

  (has-overridden-method without-method clojure.lang.IPersistentMap)

  (has-overridden-method )

  (origins (.getDeclaredMethod clojure.lang.IPersistentMap "without"
                               (class-array Class [Object])))

  (seq (.getDeclaredMethods clojure.lang.IPersistentMap))

  (interleave [Integer/TYPE] [Integer/TYPE])

  (juxt [Integer/TYPE] [Integer/TYPE])
  (has-overridden-method CharSequence)

  (methods-with-same-name-and-count
   (.getDeclaredMethod String "charAt"
                       (class-array Class [Integer/TYPE]))
   CharSequence)
  (has-overridden-method
   (.getDeclaredMethod String "charAt"
                       (class-array Class [Integer/TYPE]))
   CharSequence)
  (>pst)

  (.getDeclaredMethod CharSequence "charAt"
                      (class-array Class [Integer/TYPE]))
  (.isAssignableFrom Integer/TYPE Integer/TYPE)


  (.isAssignableFrom CharSequence String)
  (.isAssignableFrom String CharSequence)

  (base-list String)
)
