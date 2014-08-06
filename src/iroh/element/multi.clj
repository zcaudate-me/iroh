(ns iroh.element.multi
  (:require [iroh.types.element :refer :all]
            [iroh.pretty.classes :refer [class-convert]]
            [iroh.util :as util]))

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

(defmethod format-element :multi [mele]
  (format "#[%s :: %s]"
          (:name mele)
          (->> (:array mele)
               (map element-params)
               (map (fn [params] (if (empty? params) [] (apply list params))))
               (sort (fn [x y] (compare (count x) (count y))))
               (clojure.string/join ", "))))

(defmethod element-params :multi [mele]
  (map element-params (:array mele)))

(defn elegible-candidates [prelim aparams]
  (->> prelim
       (map (fn [[_ v]] v))
       (filter (fn [ele]
                 (println aparams (:params ele))
                 (every? (fn [[ptype atype]]
                           (util/param-arg-match ptype atype))
                         (map list (:params ele) aparams))))))

(defn find-method-candidate [mele aparams]
  (let [tag (if (= "new" (:name mele)) :constructor :method)
        prelim (get-in (:lookup mele) [tag (count aparams)])]
    (or (get prelim aparams)
        (get @(:cache mele) aparams)
        (if-let [ele (first (elegible-candidates prelim aparams))]
          (do (swap! (:cache mele) assoc aparams ele)
              ele)))))

(defn find-field-candidate [mele aparams]
  (if-let [ele (get-in (:lookup mele) [:field 0 []])]
    (and (or (= 0 (count aparams))
             (and (= 1 (count aparams))
                  (util/param-arg-match (:type ele) (first aparams)))))))

(defn find-candidate [mele aparams]
  (or (find-method-candidate mele aparams)
      (find-field-candidate mele aparams)
      (throw (Exception. (format "Cannot find a suitable candidate function, need %s, invoked with %s."
                                 (format-element mele)
                                 (mapv #(symbol (class-convert
                                                 % :string))
                                       aparams))))))

(defmethod invoke-element :multi [mele & args]
  (let [aparams (mapv type args)
        candidate (find-candidate mele aparams)]
    (apply candidate args)))
