(ns iroh.pretty.class)

(declare simple-name)

(defn array-name [n]
  (condp = n
    "C" "char"
    "B" "byte"
    "Z" "bool"
    "S" "short"
    "I" "int"
    "J" "long"
    "F" "float"
    "D" "double"
    (if-let [lname (second (re-find #"^L(.*);" n))]
      (simple-name lname)
      (simple-name n))))

(defn simple-name [n]
  (if (.startsWith n "[")
    (str (array-name (subs n 1)) "[]")
    n))

(defn class-name [c]
  (simple-name (.getName c)))

(defn class-string
  ([s] (class-string s false))
  ([s arr]
     (if (.endsWith s "[]")
       (str "[" (class-string
                 (subs s 0 (- (.length s) 2))true))
       (condp = s
         "char"   (if arr "C" s)
         "byte"   (if arr "B" s)
         "bool"   (if arr "Z" s)
         "short"  (if arr "S" s)
         "int"    (if arr "I" s)
         "long"   (if arr "J" s)
         "float"  (if arr "F" s)
         "double" (if arr "D" s)
         (if arr (str "L" s ";") s) ))))

(defn class-from-string [s]
  (condp = s
    "char"   Character/TYPE
    "byte"   Byte/TYPE
    "bool"   Boolean/TYPE
    "short"  Short/TYPE
    "int"    Integer/TYPE
    "long"   Long/TYPE
    "float"  Float/TYPE
    "double" Double/TYPE
    (Class/forName s)))

(defn create-class [s]
  (cond (class? s) s
        (string? s)
        (-> s
            (class-string)
            (class-from-string))
        :else
        (throw (Exception. "Input can only be a Class or String"))))
