(ns iroh.core
  (:require [iroh.core apply delegate hierarchy import query-class query-instance]
            [hara.namespace.import :as im])
  (:refer-clojure :exclude [.> .* .? .% .%> >ns >var]))

(im/import
 iroh.core.apply [.>]
 iroh.core.delegate [delegate]
 iroh.core.hierarchy [.% .%>]
 iroh.core.import [>ns >var]
 iroh.core.query-class [.?]
 iroh.core.query-instance [.*])

(comment
  (.? clojure.lang.MapEntry)
  (-> {:a 1} first type)
  (>refresh)
  (def a "hello")

  (def >a (delegate a))
  (println (:hash >a))
  (keys >a)
  (println >a)

  (.> [1 2 3 4] :root)
  (println ((delegate [1 2 3 4])))
  (.length (char-array 2))
  (>source alength)

  ;;(println ((.? String "value" :#) a (char-array "OEUOEUOEUOEUOUOEU")))
  (>a :value (char-array "aoeuaoeuoaeuaoeuoaeu"))



  (.? clojure.lang.ILookup)


  (->> (map (juxt (comp keyword :name) identity) (.* {} :field))
       (into {})))

(comment
  (.? (clojure.lang.DynamicClassLoader.))
  (.* 1)
  (:all (.* clojure.lang.DynamicClassLoader "rq" :#))
  (.> (clojure.lang.DynamicClassLoader.) .%)
  (.% (clojure.lang.DynamicClassLoader.))
  (>refresh)
  (.> {})
  (.? (type {}) #{java.util.Map} :name)

  ((.? String "new" :#) (byte-array (map byte "oeuoeu")))
  => "oeuoeu"

  (.? java.util.Map :name)

  (.* {} #{java.util.Map} :name)
  (.? clojure.lang.IPersistentMap :name)
  )
