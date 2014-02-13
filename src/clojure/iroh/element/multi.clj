(ns iroh.element.multi
  (:require [iroh.types.element :refer :all]
            [iroh.pretty.classes :refer [class-convert]]))

(defn get-name [v]
  (let [names (map :name v)]
    (assert (and (apply = names)
                 (first names))
            "All elements in vector must have the same name")
    (first names)))

(defn to-element-array [m0]
  (for [[k1 m1] (seq m0)
        [k2 m2] (seq m1)
        [k3 v]  (seq m2)]
    v))

(defn multi-element [m v]
  (element {:tag :multi
            :name (get-name v)
            :array v
            :lookup m
            :cache (atom {})}))

(defmethod to-element clojure.lang.APersistentMap [m]
  (let [v (to-element-array m)]
    (multi-element m v)))

(defn to-element-map-path [ele]
  (let [tag (:tag ele)
        params (:params ele)]
    (cond (= (:tag ele) :field)
          [tag 0 []]

          :else
          [tag (count params) params])))

(defmethod to-element clojure.lang.APersistentVector [v]
  (let [m (reduce
           (fn [m ele]
             (assoc-in m (to-element-map-path ele) ele))
           {} v)]
    (multi-element m v)))


(defmethod format-element :multi [ele]
  (format "#[%s :: %s]"
          (:name ele)
          (->> (:array ele)
               (map element-params)
               (map (fn [params] (if (empty? params) [] (apply list params))))
               (sort (fn [x y] (compare (count x) (count y))))
               (clojure.string/join ", "))))

(defmethod element-params :multi [ele]
  (map element-params (:array ele)))

(defn invoke-method-multi [ele args]
  (let [argc (count args)
        prelim (get-in (:lookup ele) [:method argc])]
    (if-let [[[_ candidate] & more] (seq prelim)]
      (if (nil? more)
        (apply candidate args)
        (recur more args)))))

(defn invoke-field-multi [ele args]
  (if-let [candidate (get-in (:lookup ele)
                             [:field 0 []])]
    (if (> 2 (count args))
      (apply candidate args))))


(defmethod invoke-element :multi [ele & args]
  (or (invoke-method-multi ele args)
      (invoke-field-multi ele args)
      (throw (Exception. (format "Cannot find a suitable candidate function, need %s, invoked with %s."
                                 (element-params ele)
                                 (mapv #(symbol (class-convert
                                                 (class %) :string))
                                       args))))))
