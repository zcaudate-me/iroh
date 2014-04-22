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
