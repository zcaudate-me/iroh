(ns iroh.core.import
  (:require [clojure.string :as string]
            [iroh.common :as common]
            [iroh.types.element :as element]
            [iroh.pretty.classes :as classes]
            [iroh.pretty.args :as args]
            [iroh.element multi method field constructor]
            [iroh.core.query-class :as q])
  (:refer-clojure :exclude [>ns >var]))

(defn element-meta [ele]
    (-> {}
        (assoc :arglists (concat (element/element-params ele)))
        (assoc :doc
          (if (= :multi (:tag ele)) ""
            (format "------------------\n\nmember: %s\n\ntype: %s\n\nmodifiers: %s"
                    (str (.getName (:container ele))
                         "/" (:name ele))
                    (classes/class-convert (:type ele) :string)
                    (string/join ", " (map name (:modifiers ele))))))))

(defmacro >var
  ([name [class method & selectors]]
     `(let [var (def ~name (q/.? ~class ~(str method) ~@selectors :#))]
        (alter-meta! var
                     (fn [~'m] (merge ~'m (element-meta ~name))))
        var))
  ([name pair & more]
     `[(iroh.core.import/>var ~name ~pair)
       ~@(map #(cons `iroh.core.import/>var %) (partition 2 more))]))

(defmacro >ns
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
