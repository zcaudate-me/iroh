(ns iroh.core
  (:require [iroh.common :refer :all]
            [iroh.hierarchy :refer :all]
            [iroh.types.element :refer [to-element element-params]]
            [iroh.pretty.classes :refer [class-convert]]
            [iroh.pretty.args :refer [args-convert args-group]]
            [iroh.pretty.display :refer [display]]
            [iroh.element multi method field constructor]))

(defmacro .> [obj & args]
  `(let [t# (type ~obj)]
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

(defmacro .? [class & selectors]
  `(list-class-elements ~class ~(args-convert selectors)))

(defn element-meta [ele]
    (-> {}
        (assoc :arglists (element-params ele))
        (assoc :doc
          (if (= :multi (:tag ele)) ""
            (format "------------------\n\nmember: %s\n\ntype: %s\n\nmodifiers: %s"
                    (str (.getName (:container ele))
                         "/" (:name ele))
                    (class-convert (:type ele) :string)
                    (clojure.string/join ", " (map name (:modifiers ele))))))))

(defmacro def.import
  ([name [class method]]
     `(let [var (def ~name (iroh.core/.? ~class ~(str method) :#))]
        (alter-meta! var
                     (fn [~'m] (merge ~'m (element-meta ~name))))
        var))
  ([name pair & more]
     `[(iroh.core/def.import ~name ~pair)
       ~@(map #(cons `iroh.core/def.import %) (partition 2 more))]))

(defmacro def.extract
  ([ns class & selectors]
     (let [home (.getName *ns*)
           eles (list-class-elements (resolve class) (args-convert selectors))
           syms (distinct (map :name eles))
           iforms (mapcat (fn [sym] [(symbol sym) [class (symbol sym)]])
                          syms)]
       `(do (clojure.core/create-ns ~(list `symbol (str ns)))
            (clojure.core/in-ns ~(list `symbol (str ns)))
            (let [vars# (iroh.core/def.import ~@iforms)]
              (clojure.core/in-ns ~(list `symbol (str home)))
              vars#)))))

(defn all-instance-elements
  [tcls icls current]
  (let [supers (inheritance-list tcls)
        eles (if current
               (list-class-elements (last supers) [:instance])
               (mapcat #(list-class-elements % [:instance]) supers))]
    (concat eles
            (if icls (concat eles (list-class-elements icls [:static]))))))

(defn list-instance-elements
  [obj selectors]
  (let [grp (args-group selectors)
        tcls (type obj)]
    (->> (all-instance-elements tcls (if (class? obj) obj) (contains? grp :current))
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

(defn instance-lookup
  ([tcls] (instance-lookup tcls nil))
  ([tcls icls] (instance-lookup tcls icls nil))
  ([tcls icls current]
      (reduce (fn [m ele]
                (assoc-in m (instance-lookup-path ele) ele)) {}
                (all-instance-elements tcls icls current))))

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

(defn apply-element [obj method args]
  (let [lu (refine-lookup (object-lookup obj))]
    (if-let [ele (get lu method)]
      (cond (:field ele)
            (apply ele obj args)

            (:static ele)
            (apply ele args)

            :else
            (apply ele obj args))
      (throw (Exception. "Element not Found.")))))

(defmacro .$ [obj method & args]
  `(apply-element ~obj ~(name method) ~args))


(comment
  (>pst)
  (keys (object-lookup (test.A.)))




  ((-> (instance-lookup sun.reflect.ReflectionFactory)
        (get "langReflectAccess")
        (:fields)))

  (def a 1)
  ((.* a :private :#) a)
  ((.* a #{Number} "shortValue"))
  ((.? Integer  2 #(= "parseInt" (:name %))) "14" 10)
  ((.? String "toCharArray" :#) "Oeuoeu")

  (def acquire-accessor (.? java.lang.reflect.Method #"acquire" :#))
  (.invoke (acquire-accessor (:delegate (.? test.A #"to" :#)))
           (test.B.) (object-array 0))
  ()

  (def to-char-array (.? String "toCharArray" :#))

  (to-char-array "oeuoeu")

  ((.? sun.reflect.NativeMethodAccessorImpl "invoke0" :#)
   (:delegate (.? test.A #"to" :#))
   (cast test.A (test.B.))
   (object-array []))

  (instance-options )
  ((.? test.B #"to" :#) (test.A.))
  ((.? test.B #"to" :#) (test.B.))
  ((.? test.A #"to" :#) (test.B.))
  ((.? test.A #"to" :#) (test.A.))
  )
