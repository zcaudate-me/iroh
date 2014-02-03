(ns iroh.element.common
  (:require [iroh.types.modifiers :refer [int-to-modifiers]]
            [iroh.pretty.class :refer [class-name]]))
(def override
  (doto (.getDeclaredField java.lang.reflect.AccessibleObject "override")
    (.setAccessible true)))

(defn set-accessible [obj flag]
  (.set override obj flag))

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
        modifiers (conj (int-to-modifiers int-m tag) tag)
        _ (set-accessible obj true)]
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
