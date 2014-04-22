(ns iroh.pretty.classes
  (require [iroh.common :refer :all]
           [iroh.pretty.primitives :refer :all]))

(def class-reps #{:raw :symbol :string :class :container})

(defn type->raw
  "type->raw"
  {:added "0.1.10"}
  [v]
  (let [raw (.getName v)]
    (or (primitive-convert raw :string :raw)
        raw)))

(declare raw->string)

(defn raw-array->string
  "raw-array->string"
  {:added "0.1.10"}
  [v]
  (if-let [obj-name (second (re-find #"^L(.*);" v))]
    (raw->string obj-name)
    (raw->string v)))

(defn raw->string
  "raw->string"
  {:added "0.1.10"}
  [v]
  (if (.startsWith v "[")
    (str (raw-array->string (subs v 1)) "[]")
    (or (primitive-convert v :raw :string)
        v)))

(defn string-array->raw
  "string-array->raw"
  {:added "0.1.10"}
  ([s] (string-array->raw s false))
  ([s arr]
     (if (.endsWith s "[]")
       (str "[" (string-array->raw
                 (subs s 0 (- (.length s) 2)) true))
       (if arr
         (or (primitive-convert s :string :raw)
             (str "L" s ";"))
         s))))

(defn string->raw
  "string->raw"
  {:added "0.1.10"}
  [v]
  (or (primitive-convert v :string :raw)
      (string-array->raw v)))

(defmulti class-convert-impl
  "class-convert-impl"
  {:added "0.1.10"}
  (fn [v to] (type v)))

(defn class-convert
  "class-convert"
  {:added "0.1.10"}
  ([v] (class-convert v :class))
  ([v to] (class-convert-impl v to)))

(defn class-convert-strin
  "class-convert-strin"
  {:added "0.1.10"}
  [v]
  (class-convert v :string))

(defmethod class-convert-impl :default
  [v to])

(defmethod class-convert-impl Class
  [v to]
  (condp = to
    :container (if (primitive-classes v)
                 (primitive-convert v :class :container)
                 v)
    :class    (if (primitive-containers v)
                (primitive-convert v :container :class)
                v)
    :symbol (class-convert (.getName v) to)
    :raw (type->raw v)
    :string (raw->string (type->raw v))))

(defmethod class-convert-impl clojure.lang.Symbol
  [v to]
  (condp = to
    :container (or (primitive-convert v :symbol :container)
                   (eval v))
    :class (or (primitive-convert v :symbol :class)
               (eval v))
    :symbol v
    :raw (string->raw (name v))
    :string (raw->string (name v))))

(defmethod class-convert-impl String
  [v to]
  (condp = to
    :container (or (primitive-convert v :raw :container)
                   (primitive-convert v :string :container)
                   (Class/forName (string->raw v)))
    :class (or (primitive-convert v :raw :class)
               (primitive-convert v :string :class)
               (Class/forName (string->raw v)))
    :symbol (or (primitive-convert v :raw :symbol)
                (primitive-convert v :string :symbol)
                (symbol (string->raw v)))
    :raw (string->raw v)
    :string (raw->string v)))
