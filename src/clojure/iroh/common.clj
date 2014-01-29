(ns iroh.common)

(defn hash-map? [x]
  (instance? clojure.lang.APersistentMap x))

(defn regex? [x]
  (instance? java.util.regex.Pattern x))

(defn assoc-if
  ([m k v] (assoc-if m k v identity))
  ([m k v f]
     (if v
       (assoc m k (f v))
       m)))

(defn select-keys-nnil [m]
  (into {}
        (filter (fn [[k v]]
                  (not (nil? v)))
                m)))

(defn- is-selected-key [vs]
  (fn [p]
    (some (fn [v]
            (or (= v p)
                (v p)))
          vs)))

(defn- select-keys-fn [coll ks]
  (let [pks (keys coll)
        pks (filter (is-selected-key ks) pks)]
    (select-keys coll pks)))

(defn select-keys-nested [coll ks]
  (cond (instance? clojure.lang.APersistentMap coll)
        (-> (->> coll
                 (map (fn [[k v]]
                        [k (select-keys-nested v ks)]))
                 (into (empty coll)))
            (select-keys-fn ks))

        (or (list? coll)
            (vector? coll)
            (instance? clojure.lang.APersistentSet coll))
        (->> coll
             (map #(select-keys-nested % ks))
             (into (empty coll)))

        :else
        coll))

(defn combinations [k l]
  (if (= 1 k) (map vector l)
      (apply concat
             (map-indexed
              #(map (fn [x] (conj x %2))
                    (combinations (dec k) (drop (inc %1) l)))
              l))))

(defn all-subsets [s]
  (apply concat
         (for [x (range 1 (inc (count s)))]
           (map #(into #{} %) (combinations x s)))))

(defn conj-fn
  ([x] (conj-fn x vector))
  ([x f]
      (fn [v]
        (if-not v (f x) (conj v x)))))
