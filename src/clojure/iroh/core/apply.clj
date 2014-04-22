(ns iroh.core.apply
  (:require [iroh.common :as common]
            [iroh.types.element :as element]
            [iroh.element multi method field constructor]
            [iroh.core.query-instance :as q]))

(defn instance-lookup-path
  "instance-lookup-path"
  {:added "0.1.10"}
  [ele]
  (let [base [(:name ele)
              (:tag ele)]]
    (cond (= (:tag ele) :field)
          (conj base 0 [])

          :else
          (let [params (:params ele)]
            (conj base (count params) params)))))

(defn assignable?
  "assignable?"
  {:added "0.1.10"}
  [current base]
  (->> (map (fn [x y]
              (or (= y x)
                  (.isAssignableFrom y x))) current base)
       (every? identity)))

(defn instance-lookup
  "instance-lookup"
  {:added "0.1.10"}
  ([tcls] (instance-lookup tcls nil))
  ([tcls icls]
     (reduce (fn [m ele]
               (if (= :method (:tag ele))
                 (let [params (:params ele)
                       params-lu (get-in m [(:name ele) :method (count params)])
                       params-list (keys params-lu)]
                   (if (some #(assignable? % params) params-list)
                     m
                     (assoc-in m (instance-lookup-path ele) ele)))
                 (assoc-in m (instance-lookup-path ele) ele)))
             {} (q/all-instance-elements tcls icls))))

(defn object-lookup
  "object-lookup"
  {:added "0.1.10"}
  [obj]
  (let [tcls (type obj)]
    (instance-lookup tcls (if (class? obj) obj))))

(defn refine-lookup
  "refine-lookup"
  {:added "0.1.10"}
  [lu]
  (let [ks (keys lu)]
    (reduce (fn [m k]
              (let [l1 (get lu k)
                    ks1 (keys l1)
                    l2 (get l1 (first ks1))
                    ks2 (keys l2)]
                (if (and (= 1 (count ks1))
                         (= 1 (count ks2)))
                  (assoc m k (-> (first ks2) (l2) (first) (second)))
                  (assoc m k (element/to-element l1))
                  )))
            {} ks)))

(defn apply-vector
  "apply-vector"
  {:added "0.1.10"}
  [obj [class method] args]
  (let [lu (refine-lookup (instance-lookup class nil))]
    (if-let [ele (get lu method)]
      "Apply Vector"
      (throw (Exception. "Element not Found.")))))

(defn get-element-lookup
  "get-element-lookup"
  {:added "0.1.10"}
  [obj]
  (let [obj-type (type obj)
        is-class   (if (class? obj) obj)]
    (if-let [lu (get-in @common/*cache* [obj-type is-class])]
      lu
      (let [lu (refine-lookup (object-lookup obj))]
        (swap! common/*cache* (fn [m]
                        (assoc-in m [obj-type is-class] lu)))
        lu))))

(defn apply-element
  "apply-element"
  {:added "0.1.10"}
  [obj method args]
  (let [lu (get-element-lookup obj)]
    (if-let [ele (get lu method)]
      (cond (-> ele :modifiers :field)
            (apply ele obj args)

            (:static ele)
            (apply ele args)

            :else
            (apply ele obj args))
      (throw (Exception. (format "Class member not Found for %s - `%s`" (common/context-class obj) method))))))

(defmacro .>
  ".>"
  {:added "0.1.10"}
  ([obj] obj)
  ([obj method]
     (cond (not (list? method))
           `(.> ~obj (~method))

           (list? method)
           (let [[method & args] method]
             (cond (#{'.* '.? '.% '.%>} method)
                   `(~(symbol (str "iroh.core/" method)) ~obj ~@args)

                   (and (symbol? method) (.startsWith (name method) "."))
                   `(apply-element ~obj ~(subs (name method) 1) ~(vec args))

                   (keyword? method)
                   `(or (~method ~obj ~@args)
                        (let [nm# ~(subs (str method) 1)]
                          (println nm#)
                          (if (some #(= % nm#) (.* ~obj :name))
                            (apply-element ~obj nm# ~(vec args)))))

                   :else
                   `(~method ~obj ~@args)
                   ))))

  ([obj method & more]
     `(.> (.> ~obj ~method) ~@more)))
