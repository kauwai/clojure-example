(ns main
  (:gen-class)
  (:require [clojure-lsp.api :as api]
            [clojure-lsp.queries :as queries]
            [clojure-lsp.internal-api :refer [db*]])
  (:import (java.io File)))

(defn var-usages [dump-result]
  (->> dump-result
       :analysis
       vals
       (map :var-usages)
       flatten
       (filter #(and (:from %) (:from-var %) (:to %) (:name %)))
       (remove #(.startsWith (name (:to %)) "clojure."))))

(defn external-vars-definitions [dump-result]
  (->> (var-usages dump-result)
       (map (partial queries/find-definition dump-result))))

(defn dump-result [workspace]
  (reset! db* nil)
  (api/analyze-project-and-deps!
   {:project-root (File. workspace)
    :analysis :project-only})
  (-> (deref db*)
      (assoc :project-root workspace)))

(defn- -main
  [& args]
  (let [params (apply hash-map args)
        workspace (get params "-workspace")
        result (external-vars-definitions (dump-result (.getAbsolutePath (File. workspace))))]
    (prn result)))
