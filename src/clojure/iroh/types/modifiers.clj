(ns iroh.types.modifiers)

(def flags
  {:plain          2r000000000000
   :public         2r000000000001    ;; java.lang.reflect.Modifier/PUBLIC
   :private        2r000000000010    ;; java.lang.reflect.Modifier/PRIVATE
   :protected      2r000000000100    ;; java.lang.reflect.Modifier/PROTECTED
   :static         2r000000001000    ;; java.lang.reflect.Modifier/STATIC
   :final          2r000000010000    ;; java.lang.reflect.Modifier/FINAL
   :synchronized   2r000000100000    ;; java.lang.reflect.Modifier/SYNCHRONIZE

   :native         2r000100000000    ;; java.lang.reflect.Modifier/NATIVE
   :interface      2r001000000000    ;; java.lang.reflect.Modifier/INTERFACE
   :abstract       2r010000000000    ;; java.lang.reflect.Modifier/ABSTRACT
   :strict         2r100000000000    ;; java.lang.reflect.Modifier/STRICT

   :synthetic      0x1000        ;; java.lang.Class/SYNTHETIC
   :annotation     0x2000        ;; java.lang.Class/ANNOTATION
   :enum           0x4000        ;; java.lang.Class/ENUM
   })

(def field-flags
  {:volatile       2r000001000000    ;; java.lang.reflect.Modifier/VOLATILE
   :transient      2r000010000000    ;; java.lang.reflect.Modifier/TRANSIENT
   })

(def method-flags
  {:bridge         2r000001000000    ;; java.lang.reflect.Modifier/BRIDGE
   :varargs        2r000010000000    ;; java.lang.reflect.Modifier/VARARGS
   })

(defn- get-modifiers
  [int [[k v] & more] output]
  (if (nil? k) output
      (recur int more
             (if (zero? (bit-and v int))
               output
               (conj output k)))))

(defn int-to-modifiers
  ([int] (int-to-modifiers int nil))
  ([int method]
     (let [output (get-modifiers int (seq flags) #{})]
       (condp = method
         :method (get-modifiers int (seq method-flags) output)
         :field  (get-modifiers int (seq field-flags) output)
         output))))

(defn modifiers-to-int
  [modifiers]
  (reduce (fn [i x]
            (bit-or i (or (get flags x)
                          (get field-flags x)
                          (get method-flags x)
                          0)))
          0 modifiers))