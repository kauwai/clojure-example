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
       (filter #(= (name (:name %)) "read-raw"))))

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
  [& _]
  (let [workspace (System/getProperty "user.dir")
        result (external-vars-definitions (dump-result workspace))]
    (prn result)))
