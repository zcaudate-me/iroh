(ns iroh.pretty.classes
  (require [iroh.common :refer :all]
           [iroh.pretty.primitives :refer :all]))

(def class-reps #{:raw :symbol :string :class :container})

(defn type->raw
  "converts to the raw representation

  (type->raw Class) => \"java.lang.Class\"
  (type->raw 'byte) => \"B\""
  {:added "0.1.10"}
  [v]
  (let [raw (.getName v)]
    (or (primitive-convert raw :string :raw)
        raw)))

(declare raw->string)

(defn raw-array->string
  "converts the raw representation to a more readable form

  (raw-array->string \"[[B\") => \"byte[][]\"
  (raw-array->string \"[Ljava.lang.Class;\") => \"java.lang.Class[]\""
  {:added "0.1.10"}
  [v]
  (if-let [obj-name (second (re-find #"^L(.*);" v))]
    (raw->string obj-name)
    (raw->string v)))

(defn raw->string
  "converts the raw array representation to a human readable form

  (raw->string \"[[V\") => \"void[][]\"
  (raw->string \"[Ljava.lang.String;\") => \"java.lang.String[]\""
  {:added "0.1.10"}
  [v]
  (if (.startsWith v "[")
    (str (raw-array->string (subs v 1)) "[]")
    (or (primitive-convert v :raw :string)
        v)))

(defn string-array->raw
  "converts the human readable form to a raw string

  (string-array->raw \"java.lang.String[]\")[Ljava.lang.String;"
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
  "converts any string to it's raw representation

  (string->raw \"java.lang.String[]\") => \"[Ljava.lang.String;\"

  (string->raw \"int[][][]\") => \"[[[I\""
  {:added "0.1.10"}
  [v]
  (or (primitive-convert v :string :raw)
      (string-array->raw v)))

(defmulti class-convert-impl
  "converts a string to its representation. Implementation function

  (class-convert-impl Class  :string) => \"java.lang.Class\"

  (class-convert-impl \"byte\" :class) => Byte/TYPE

  (class-convert-impl \"byte\" :container) => Byte"
  {:added "0.1.10"}
  (fn [v to] (type v)))

(defn class-convert
  "Converts a class to its representation.

  (class-convert \"byte\") => Byte/TYPE

  (class-convert 'byte :string) => \"byte\"

  (class-convert (Class/forName \"[[B\") :string) => \"byte[][]\""
  {:added "0.1.10"}
  ([v] (class-convert v :class))
  ([v to] (class-convert-impl v to)))

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
