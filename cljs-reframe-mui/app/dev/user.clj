(ns user)

(defn dev
  []
  (require '[dev])
  (in-ns 'dev))

(defn mongo
  []
  (require '[mongo])
  (in-ns 'mongo))

(defn tree
  []
  (require '[tree])
  (in-ns 'tree))

(defn playground
  []
  (require '[playground])
  (in-ns 'playground))
