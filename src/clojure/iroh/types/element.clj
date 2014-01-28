(ns iroh.types.element
  (:require [iroh.common :refer :all]))

(defmulti invoke-element (fn [x & args] (:tag x)))

(defmulti to-element (fn [obj] (type obj)))

(defn make-invoke-element-form [args]
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

(defmacro init-element-type [n]
  (concat
   '(deftype Element [body]
      clojure.lang.ILookup
      (valAt [ele k]
        (if k (get body k) body))

      clojure.lang.IFn)
   (map make-invoke-element-form
        (for [l (range n)]
          (vec (for [x (range l)]
                 (symbol (str "arg" x))))))))

(init-element-type 20)

(defn element [body]
  (Element. body))

(defmulti format-element (fn [ele] (:tag ele)))

(defmethod print-method Element [v w]
  (.write w (format-element v)))
