(ns iroh.element.common
  (:require [iroh.types.modifiers :refer [int-to-modifiers]]))

(defn add-annotations [seed obj]
  (if-let [anns (seq (.getDeclaredAnnotations obj))]
    (->> anns
         (map (fn [ann] [(.annotationType ann)
                        (str ann)]))
         (into {})
         (assoc seed :annotations))
    seed))

(defn seed [tag obj]
  (let [int-m (.getModifiers obj)
        modifiers (conj (int-to-modifiers int-m) tag)
        _ (.setAccessible obj true)]
    (-> {:name (.getName obj)
         :tag  tag
         :hash (.hashCode obj)
         :container (.getDeclaringClass obj)
         :modifiers modifiers
         :static  (contains? modifiers :static)
         :delegate obj}
        (add-annotations obj))))

(declare simple-name)

(defn array-name [n]
  (condp = n
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

(defn prepare-params [ele]
  (let [params (or (:params ele) [])
        params (if (:static ele)
                 params
                 (concat [(:container ele)] params))]
    (apply list (map class-name params))))

(defn class-string
  ([s] (class-string s false))
  ([s arr]
     (if (.endsWith s "[]")
       (str "[" (class-string
                 (subs s 0 (- (.length s) 2))true))
       (condp = s
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



(comment

  (type (byte-array [])) ;;=> [B
  (type (boolean-array [])) ;;=> [Z
  (type (int-array [])) ;;=> [I
  (type (float-array [])) ;;=> [F
  (type (double-array [])) ;;=> [D
  (type (short-array [])) ;;=> [S
  (class-name (type (long-array []))) ;;=> [J
  (class-name (Class/forName "[[[[[Ljava.lang.Object;")) ;;=> [J

  ;;=> [Ljava.lang.Object;

  (-> (filter-elements
       (group-arguments [#"copy"])
       (map to-element
            (seq (.getDeclaredMethods java.lang.Class))))
      first
      :params
      first
      (.getName))
)
