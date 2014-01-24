(ns iroh.core)

(def *static-desc* (atom {}))
(def *instance-desc* (atom {}))

(defn dot-star [obj & selectors])

(defmacro .* [obj & selectors]
  `(dot-star ~obj ~@selectors))

(defn dot-hash [class & selectors])

(defmacro .# [class & selectors]
  `(dot-hash ~class ~@selectors))

(defn dot-dollar [obj method & args])

(defmacro .$ [obj method & args]
  `(dot-dollar ~obj ~method ~@args))

(defmacro .$> [obj & forms])


(.* 1)

(.# Object :private)
