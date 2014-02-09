(ns iroh.pretty.args
  (:require [iroh.common :refer :all]
            [iroh.pretty.primitives :refer [primitive-convert]]
            [iroh.pretty.classes :refer [class-convert]]))

(def sort-terms #{:by-name :by-params :by-modifiers :by-type})

(def select-terms #{:tag
                    :name
                    :params
                    :modifiers
                    :type
                    :attributes
                    :origins
                    :container
                    :delegate})

(defn args-classify [arg]
  (cond (sort-terms arg)                 :sort-terms
        (select-terms arg)               :select-terms
        (= :first arg)                   :first
        (or (= :# arg) (= :merge arg))   :merge

        (or (string? arg) (regex? arg))  :name
        (fn? arg)                        :predicate
        (set? arg)                       :origins
        (and (vector? arg)
             (= :any (first arg)))       :any-params
        (and (vector? arg)
             (= :all (first arg)))       :all-params
        (number? arg)                    :num-params
        (vector? arg)                    :params

        (or (class? arg) (symbol? arg))  :type

        (keyword? arg)                   :modifiers
        (hash-map? arg)                  :attribute))

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
