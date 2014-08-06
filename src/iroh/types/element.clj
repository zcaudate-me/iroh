(ns iroh.types.element
  (:require [iroh.common :refer :all]
            [clojure.walk :as walk]))

(defmulti invoke-element
  (fn [x & args] (:tag x)))

(defmulti invoke-element
  (fn [x & args] (:tag x)))

(defmulti to-element type)

(defmulti element-params :tag)

(defmulti format-element :tag)

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
  [body]
  (Element. body))

(defn element?
  [x]
  (instance? Element x))

(defmethod print-method Element [v w]
  (.write w (str v)))
