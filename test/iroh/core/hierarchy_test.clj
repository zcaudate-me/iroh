(ns iroh.core.hierarchy-test
  (:use midje.sweet)
  (:require [iroh.core.hierarchy :refer :all]))

^{:refer iroh.core.hierarchy/.% :added "0.1.10"}
(fact ".%")

^{:refer iroh.core.hierarchy/.%> :added "0.1.10"}
(fact ".%>")