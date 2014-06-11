(ns iroh.core.import
  (:require [clojure.string :as string]
            [iroh.common :as common]
            [iroh.types.element :as element]
            [iroh.pretty.classes :as classes]
            [iroh.pretty.args :as args]
            [iroh.element multi method field constructor]
            [iroh.core.query-class :as q])
  (:refer-clojure :exclude [>ns >var]))

(defn process-if-single [args]
  (if (and (vector? (first (first args)))
           (= 1 (count (first args))))
    (first args)
    args))

(defn element-meta
  [ele]
  (-> {}
      (assoc :arglists (->> (element/element-params ele)
                            concat
                            set
                            (mapv vec)
                            (process-if-single)))
      (assoc :doc
        (if (= :multi (:tag ele)) ""
          (format "\nmember: %s\ntype: %s\nmodifiers: %s"
                  (str (.getName (:container ele))
                       "/" (:name ele))
                  (classes/class-convert (:type ele) :string)
                  (string/join ", " (map name (:modifiers ele))))))))

(defmacro >var
  "imports a class method into the current namespace.

  (>var hash-without [clojure.lang.IPersistentMap without])

  (with-out-str (clojure.repl/doc hash-without))
  => (str \"-------------------------\\n\"
          \"iroh.core.import-test/hash-without\\n\"
          \"([clojure.lang.IPersistentMap java.lang.Object])\\n\"
          \"  \\n\"
          \"member: clojure.lang.IPersistentMap/without\\n\"
          \"type: clojure.lang.IPersistentMap\\n\"
          \"modifiers: instance, method, public, abstract\\n\")

  (eval '(hash-without {:a 1 :b 2} :a))
  => {:b 2}"
  {:added "0.1.10"}
  ([name [class method & selectors]]
     `(let [var# (def ~name (q/.? ~class ~(str method) ~@selectors :#))]
        (alter-meta! var#
                     (fn [m#] (merge m# (element-meta ~name))))
        var#))
  ([name pair & more]
     `[(iroh.core.import/>var ~name ~pair)
       ~@(map #(cons `iroh.core.import/>var %) (partition 2 more))]))

(defmacro >ns
  "imports all class methods into its own namespace.

  (map #(.sym %)
       (>ns test.string String :private #\"serial\"))
  => '[serialPersistentFields serialVersionUID]"
  {:added "0.1.10"}
  ([ns class & selectors]
     (let [home (.getName *ns*)
           eles (q/list-class-elements (resolve class) (args/args-convert selectors))
           syms (distinct (map :name eles))
           iforms (mapcat (fn [sym] [(symbol sym) [class (symbol sym)]])
                          syms)]
       `(do (clojure.core/create-ns ~(list `symbol (str ns)))
            (clojure.core/in-ns ~(list `symbol (str ns)))
            (let [vars# (iroh.core.import/>var ~@iforms)]
              (clojure.core/in-ns ~(list `symbol (str home)))
              vars#)))))
