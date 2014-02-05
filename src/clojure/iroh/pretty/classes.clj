(ns iroh.pretty.classes
  (require [iroh.common :refer :all]
           [iroh.pretty.primitives :refer [primitive-convert]]))

(def class-reps #{:raw :symbol :string :type})

(defn type->raw [v]
  (let [raw (.getName v)]
    (or (primitive-convert raw :string :raw)
        raw)))

(defn raw-array->string [v]
  (if-let [obj-name (second (re-find #"^L(.*);" v))]
    (raw->string obj-name)
    (raw->string v)))

(defn raw->string [v]
  (if (.startsWith v "[")
    (str (raw-array->string (subs v 1)) "[]")
    (or (primitive-convert v :raw :string)
        v)))

(defn string-array->raw
  ([s] (string-array->raw s false))
  ([s arr]
     (if (.endsWith s "[]")
       (str "[" (string-array->raw
                 (subs s 0 (- (.length s) 2)) true))
       (if arr
         (or (primitive-convert s :string :raw)
             (str "L" s ";"))
         s))))

(defn string->raw [v]
  (or (primitive-convert v :string :raw)
      (string-array->raw v)))

(defmulti class-convert (fn [v to] (type v)))

(defmethod class-convert Class
  [v to]
  (condp = to
    :type v
    :symbol (class-convert (.getName v) to)
    :raw (type->raw v)
    :string (raw->string (type->raw v))))

(defmethod class-convert clojure.lang.Symbol
  [v to]
  (condp = to
    :type (eval v)
    :symbol v
    :raw (string->raw (name v))
    :string (raw->string (name v))))

(defmethod class-convert String
  [v to]
  (condp = to
    :type (or (primitive-convert v :raw :type)
              (primitive-convert v :string :type)
              (Class/forName (string->raw v)))
    :symbol (or (primitive-convert v :raw :symbol)
                (primitive-convert v :string :symbol)
                (symbol (string->raw v)))
    :raw (string->raw v)
    :string (raw->string v)))
