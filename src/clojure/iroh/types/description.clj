(ns iroh.types.description
  (:require [iroh.common :refer :all]))

(defn description-lookup [data lookup k]
  (cond (instance? clojure.lang.IPersistentSet k)
        (get-in lookup [:by-modifier k])

        (keyword? k)
        (cond (= :lookup k) lookup
              (= :data k)   data
              :else
              (get data k))

        (string? k)
        (get-in lookup [:by-category k])

        (symbol? k)
        (get data (name k))))

(defmulti invoke-description (fn [x & args] (:tag x)))

(defn make-invoke-description-form [args]
  (clojure.walk/postwalk
   (fn [x]
     (cond (and (list? x)
                (= 'invoke-description (first x)))
           (concat x args)

           (vector? x)
           (vec (concat x args))
           :else x))
   '(invoke [ele]
            (invoke-description ele))))

(defmacro init-description-type [n]
  (concat
   '(deftype Description [tag data lookup]
      clojure.lang.ILookup
      (valAt [ele k]
        (description-lookup data lookup k))

      clojure.lang.IFn)
   (map make-invoke-description-form
        (for [l (range n)]
          (vec (for [x (range l)]
                 (symbol (str "arg" x))))))))

(init-description-type 20)

(defn description [tag data lookup]
  (Description. tag data lookup))

(defmethod print-method Description [v w]
  (.write w (str "@DESC" (dissoc (select-keys-nnil (.data v)) :class :parent))))