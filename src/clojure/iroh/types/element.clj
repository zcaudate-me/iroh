(ns iroh.types.element
  (:require [iroh.common :refer :all]
            [clojure.walk :as walk]))

            (defmulti invoke-element
  "invoke-element"
  {:added "0.1.10"}
  (fn [x & args] (:tag x)))

(defmulti invoke-element
  "invoke-element"
  {:added "0.1.10"}
  (fn [x & args] (:tag x)))

(defmulti to-element
  "to-element"
  {:added "0.1.10"}
  type)

(defmulti element-params
  "element-params"
  {:added "0.1.10"}
  :tag)

(defmulti format-element
  "format-element"
  {:added "0.1.10"}
  :tag)

(defn make-invoke-element-form
  [args]
  (clojure.walk/postwalk
   (fn [x]
     (cond (and (list? x)
                (= 'invoke-element (first x)))
           (concat x args)

           (vector? x)
           (vec (concat x args))
           :else x))
   '(invoke [ele]
            (invoke-element ele))))

(defmacro init-element-type
  [n]
  (concat
   '(deftype Element [body]
      java.lang.Object
      (toString [ele]
        (format-element ele))

      clojure.lang.ILookup
      (valAt [ele k]
        (if (or (nil? k)
                (= k :all))
          body
          (get body k)))

      clojure.lang.IFn
      (applyTo [ele args]
        (clojure.lang.AFn/applyToHelper ele args)))
   (map make-invoke-element-form
        (for [l (range n)]
          (vec (for [x (range l)]
                 (symbol (str "arg" x))))))))

(init-element-type 20)

(defn element
  "element"
  {:added "0.1.10"}
  [body]
  (Element. body))

(defn element?
  "element?"
  {:added "0.1.10"}
  [x]
  (instance? Element x))

(defmethod print-method Element [v w]
  (.write w (str v)))
