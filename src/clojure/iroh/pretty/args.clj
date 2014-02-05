(ns iroh.pretty.args
  (:require [iroh.common :refer :all]
            [iroh.pretty.primitives :refer [primitive-convert]]
            [iroh.pretty.classes :refer [class-convert]]))

(def sort-terms #{:by-name :by-params :by-modifiers :by-type})

(def display-terms #{:name :params :modifiers :type :attributes})

(defn args-classify [arg]
  (cond (sort-terms arg)                 :sort-terms
        (display-terms arg)              :display-terms
        (number? arg)                    :num-args
        (or (= :# arg) (= :first arg))   :first
        (keyword? arg)                   :modifiers
        (or (string? arg) (regex? arg))  :name
        (or (set? arg) (vector? arg))    :params
        (hash-map? arg)                  :attributes
        (or (class? arg) (symbol? arg))  :type))

(defn args-convert [args]
  (let [single-fn #(or (class-convert %) %)]
  (mapv (fn [v]
          (cond (vector? v)
                (mapv single-fn v)
                      
                (set? v)
                (set (map single-fn v))
                          
                (symbol? v)
                (or (primitive-convert v :symbol :class)
                    v)
                :else v))
        args)))

(defn args-group [args]
  (-> (group-by args-classify args)
      (update-in-if [:params] args-convert)
      (update-in-if [:type] args-convert)))
