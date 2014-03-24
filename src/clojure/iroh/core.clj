(ns iroh.core
  (:require [iroh.common :refer :all]
            [iroh.hierarchy :refer :all]
            [iroh.types.element :refer [to-element element-params]]
            [iroh.pretty.classes :refer [class-convert]]
            [iroh.pretty.args :refer [args-convert args-group]]
            [iroh.pretty.display :refer [display]]
            [iroh.element multi method field constructor])
  (:refer-clojure :exclude [.> .* .? .% .%> >ns >var]))

(def ^:dynamic *cache* (atom {}))

(defmacro .% [obj]
  `(iroh.element.common/seed :class (context-class ~obj)))

(defmacro .%> [obj & args]
  `(let [t# (context-class ~obj)]
     (vec (concat [t#] (base-list t#)))))

(defn all-class-members [class]
  (concat
     (seq (.getDeclaredMethods class))
     (seq (.getDeclaredConstructors class))
     (seq (.getDeclaredFields class))))

(defn list-class-elements
  ([class]
     (->> (all-class-members class)
          (map to-element)))
  ([class selectors]
     (let [grp (args-group selectors)]
       (->> (list-class-elements class)
            (display grp)))))

(defmacro .? [obj & selectors]
  `(list-class-elements (context-class ~obj) ~(args-convert selectors)))

(defn element-meta [ele]
    (-> {}
        (assoc :arglists (concat (element-params ele)))
        (assoc :doc
          (if (= :multi (:tag ele)) ""
            (format "------------------\n\nmember: %s\n\ntype: %s\n\nmodifiers: %s"
                    (str (.getName (:container ele))
                         "/" (:name ele))
                    (class-convert (:type ele) :string)
                    (clojure.string/join ", " (map name (:modifiers ele))))))))

(defmacro >var
  ([name [class method & selectors]]
     `(let [var (def ~name (iroh.core/.? ~class ~(str method) ~@selectors :#))]
        (alter-meta! var
                     (fn [~'m] (merge ~'m (element-meta ~name))))
        var))
  ([name pair & more]
     `[(iroh.core/>var ~name ~pair)
       ~@(map #(cons `iroh.core/>var %) (partition 2 more))]))

(defmacro >ns
  ([ns class & selectors]
     (let [home (.getName *ns*)
           eles (list-class-elements (resolve class) (args-convert selectors))
           syms (distinct (map :name eles))
           iforms (mapcat (fn [sym] [(symbol sym) [class (symbol sym)]])
                          syms)]
       `(do (clojure.core/create-ns ~(list `symbol (str ns)))
            (clojure.core/in-ns ~(list `symbol (str ns)))
            (let [vars# (iroh.core/>var ~@iforms)]
              (clojure.core/in-ns ~(list `symbol (str home)))
              vars#)))))

(defn all-instance-elements
  [tcls icls]
  (let [supers (reverse (inheritance-list tcls))
        eles   (mapcat #(list-class-elements % [:instance]) supers)]
    (concat eles
            (if icls (concat eles (list-class-elements icls [:static]))))))

(defn list-instance-elements
  [obj selectors]
  (let [grp (args-group selectors)
        tcls (type obj)]
    (->> (all-instance-elements tcls (if (class? obj) obj))
         (display grp))))

(defmacro .* [obj & selectors]
  `(list-instance-elements ~obj ~(args-convert selectors)))

(defn instance-lookup-path [ele]
  (let [base [(:name ele)
              (:tag ele)]]
    (cond (= (:tag ele) :field)
          (conj base 0 [])

          :else
          (let [params (:params ele)]
            (conj base (count params) params)))))

(defn assignable? [current base]
  (->> (map (fn [x y]
              (or (= y x)
                  (.isAssignableFrom y x))) current base)
       (every? identity)))

(defn instance-lookup
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
             {} (all-instance-elements tcls icls))))

(defn object-lookup [obj]
  (let [tcls (type obj)]
    (instance-lookup tcls (if (class? obj) obj))))

(defn refine-lookup [lu]
  (let [ks (keys lu)]
    (reduce (fn [m k]
              (let [l1 (get lu k)
                    ks1 (keys l1)
                    l2 (get l1 (first ks1))
                    ks2 (keys l2)]
                (if (and (= 1 (count ks1))
                         (= 1 (count ks2)))
                  (assoc m k (-> (first ks2) (l2) (first) (second)))
                  (assoc m k (to-element l1))
                  )))
            {} ks)))

(defn apply-vector [obj [class method] args]
  (let [lu (refine-lookup (instance-lookup class nil))]
    (if-let [ele (get lu method)]
      "Apply Vector"
      (throw (Exception. "Element not Found.")))))

(defn get-element-lookup [obj]
  (let [obj-type (type obj)
        is-class   (if (class? obj) obj)]
    (if-let [lu (get-in @*cache* [obj-type is-class])]
      lu
      (let [lu (refine-lookup (object-lookup obj))]
        (swap! *cache* (fn [m]
                        (assoc-in m [obj-type is-class] lu)))
        lu))))

(defn apply-element [obj method args]
  (let [lu (get-element-lookup obj)]
    (if-let [ele (get lu method)]
      (cond (-> ele :modifiers :field)
            (apply ele obj args)

            (:static ele)
            (apply ele args)

            :else
            (apply ele obj args))
      (throw (Exception. (format "Class member not Found for %s - `%s`" (context-class obj) method))))))

(defmacro .>
  ([obj] obj)
  ([obj method]
     (cond (symbol? method)
           `(.> ~obj (~method))

           (list? method)
           (let [[method & args] method]
             (cond (#{'.* '.? '.% '.%>} method)
                   `(~(symbol (str "iroh.core/" method)) ~obj ~@args)

                   (.startsWith (name method) ".")
                   `(apply-element ~obj ~(subs (name method) 1) ~(vec args))

                   :else
                   `(~method ~obj ~@args)
                   ))))

  ([obj method & more]
     `(.> (.> ~obj ~method) ~@more)))


(comment
  (.? (clojure.lang.DynamicClassLoader.))
  (.* 1)
  (:all (.* clojure.lang.DynamicClassLoader "rq" :#))
  (.> (clojure.lang.DynamicClassLoader.) .%)
  (.% (clojure.lang.DynamicClassLoader.))
  (>refresh)
  (.> {})
  (.? (type {}) #{java.util.Map} :name)

  ((.? String "new" :#) (byte-array (map byte "oeuoeu")))
  => "oeuoeu"

  (.? java.util.Map :name)

  (.* {} #{java.util.Map} :name)



  (.? clojure.lang.IPersistentMap :name)
  )
