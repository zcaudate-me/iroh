(ns iroh.element.common
  (:require [iroh.types.modifiers :refer [int-to-modifiers]]
            [iroh.util.class :refer [class-name]]))

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
        
(defn prepare-params [ele]
  (let [params (or (:params ele) [])
        params (if (:static ele)
                 params
                 (concat [(:container ele)] params))]
    (apply list (map class-name params))))
