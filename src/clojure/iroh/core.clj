(ns iroh.core
  (:require [iroh.common :refer :all]
            [iroh.hierarchy :refer :all]
            [iroh.types.element :refer [to-element element-params]]
            [iroh.pretty.classes :refer [class-convert]]
            [iroh.pretty.args :refer [args-convert args-group]]
            [iroh.pretty.display :refer [display]]
            [iroh.element multi method field constructor]))

(def *cache* (atom {}))

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
      (cond (:field ele)
            (apply ele obj args)

            (:static ele)
            (apply ele args)

            :else
            (apply ele obj args))
      (throw (Exception. "Element not Found.")))))

(defmacro .$ [method obj & args]
  (if (vector? method)
    `(apply-vector ~obj [~(first method) ~(name (second method))] ~(vec args))
    `(apply-element ~obj ~(name method) ~(vec args))))

(comment
  (def direct-handle (.? java.lang.invoke.DirectMethodHandle "new" :#))
  (def method-type (.? java.lang.invoke.MethodType "makeImpl" :#))
  (def member-from-method (.? java.lang.invoke.MemberName "new"
                              [java.lang.reflect.Method] :#))

  (def obj-member-name
    (member-from-method (:delegate (.? Object "toString" :#))))
  (def obj-type (method-type String (class-array Class [Object]) true))
  (def obj-handle (direct-handle obj-type obj-member-name false Object))
  (invoke obj-handle 1) ;;=> "java.lang.Long@1"
  (invoke obj-handle 100) ;;=> "java.lang.Long@64"


  (def member-name (.? java.lang.invoke.MemberName "new" [Class String java.lang.invoke.MethodType] :#))
  (def str-type (method-type String (class-array Class [String]) true))
  (def str-member-name
    (member-from-method (:delegate (.? String "toString" :#))))
  (def str-handle (direct-handle str-type str-member-name false String))

  (defn invoke [^java.lang.invoke.MethodHandle handle & args]
    (.invokeWithArguments handle (object-array args)))

  (invoke obj-handle "oeuoeu")
  (invoke str-handle "oeuoeueo")

  (java.lang.invoke.MethodType.)

  (>pst)
  (keys (object-lookup (test.A.)))

  ((-> (instance-lookup sun.reflect.ReflectionFactory)
        (get "langReflectAccess")
        (:fields)))

  (.$ toString "1")

  (.$ without {:a 1} :a)

  (def handles-lu (java.lang.invoke.MethodHandles/lookup))
  (.invoke (.unreflect handles-lu (:delegate (.? Object "equals" :#)))
           (object-array ["1"]))

  (def a 1)
  (>pst)
  ((.* a #{String}) a)
  ((.* a #{Number} "shortValue"))
  ((.? Integer  2 #(= "parseInt" (:name %))) "14" 10)
  ((.? String "toCharArray" :#) "Oeuoeu")

  (def acquire-accessor (.? java.lang.reflect.Method #"acquire" :#))
  (.invoke (acquire-accessor (:delegate (.? test.A #"to" :#)))
           (test.B.) (object-array 0))

  (reimport 'im.chit.iroh.Util
            'test.A
            'test.B)
  (Util/invokeMethod (:delegate (.? test.B #"to" :#)) test. (list (test.B.)))
  (.findSpecial (java.lang.invoke.MethodHandles/lookup) A "toString" )

  (.invokeWithArguments
   (.findSpecial (java.lang.invoke.MethodHandles/lookup)
                 A "toString" (java.lang.invoke.MethodType/methodType String)
                 Object)
   (to-array (test.B.)))

  (def to-char-ar
    ray (.? String "toCharArray" :#))

  (to-char-array "oeuoeu")
  ((.* "oueu" "toString" [String] :#) "oeuoeu")
  ((.* "oueu" "toString" [Object] :#) "oeuoeu")

  ((.? sun.reflect.NativeMethodAccessorImpl "invoke0" :#)
   (:delegate (.? test.A #"to" :#))
   (cast test.A (test.B.))
   (object-array []))

  (instance-options )
  ((.? Object #"to" :#) (test.A.))
  ((.? test.B #"to" :#) (test.B.))
  ((.? test.A #"to" :#) (test.B.))
  ((.? test.A #"to" :#) (test.A.)))
