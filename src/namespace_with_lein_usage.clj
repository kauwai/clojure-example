(ns namespace-with-lein-usage
  (:require [leiningen.core.project :as lein]))

(defn- fn-using-lein []
  (lein/read-raw ""))
