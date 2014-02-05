(ns iroh.pretty.class
  (require [iroh.common :refer :all]))

(declare simple-name)

(defn array-name [n]
  (or (primitive-convert n :raw :string)
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
                 (subs s 0 (- (.length s) 2)) true))
       (if arr
         (or (primitive-convert s :string :raw)
             (str "L" s ";"))
         s))))

(defn class-from-string [s]
  (or (primitive-convert s :string :type)
      (Class/forName s)))

(defn create-class [s]
  (cond (class? s) s

        (symbol? s)
        (or (primitive-convert s :symbol :type)
            (eval s))

        (string? s)
        (-> s
            (class-string)
            (class-from-string))
        :else
        (throw (Exception. "Input can only be a Class or String"))))
