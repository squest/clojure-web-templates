(ns app.backsite.commons.tree-processor
  (:require [app.utils :refer :all]))

;; Transforming flat data structure into tree and vice versa
;; Normally used for skill-groups and content-folders
(defn flatten-tree
  "Recursive function to flatten a tree into a flat data structure with children only at one level down,
   and children containing only their IDs. Returns a vector."
  ([data parent-id]
   (let [children (:children data)]
     (if (empty? children)
       [(assoc data :parent parent-id)]
       (let [new-data (assoc data
                        :parent parent-id
                        :children (mapv :_id children))]
         (vec (concat [new-data] (mapcat #(flatten-tree % (:_id data)) children)))))))
  ([data]
   (let [children (:children data)]
     (if (empty? children)
       [data]
       (let [new-data (assoc data :children (mapv :_id children))]
         (vec (concat [new-data] (mapcat #(flatten-tree % (:_id data)) children))))))))

(defn tree->flat
  "Transforms a tree into a flat data structure with children only at one level down,
   and children containing only their IDs. Returns a vector."
  [data]
  (vec (mapcat #(flatten-tree %) data)))

(defn find-children
  [flattened parent]
  (filterv #(= (:parent %) parent) flattened))

(defn unflatten
  [flattened item]
  (let [children (find-children flattened (:_id item))]
    (assoc item :children (mapv #(unflatten flattened %) children))))

(defn flat->tree
  "Transforms a flat data structure into a tree. Returns a vector."
  [data]
  (let [roots (find-children data nil)]
    (mapv #(unflatten data %) roots)))

;; Below are functions for adding, editing and removing folders and skill-groups
;; So far the plan is not to use them


(defn add-node-recursive
  "Recursive function to add a node to a tree. Returns a vector."
  [nodes parent-id {:keys [name _id]}]
  (if-let [parent-node (first (filter #(= (:_id %) parent-id) nodes))]
    (-> (fn [node]
          (if (= (:_id node) parent-id)
            (update node
                    :children
                    #(conj % {:_id        _id
                              :name       name
                              :parent     parent-id
                              :node-level (inc (:node-level node))
                              :children   []}))
            node))
        (mapv nodes))
    (-> (fn [node]
          (update node
                  :children
                  #(add-node-recursive % parent-id {:name name :_id _id})))
        (mapv nodes))))

(defn add-node
  "Add node into a tree structure cf/sg structure.
  Returns a new tree with the added node."
  ([nodes {:keys [name _id]}]
   (dosync (alter nodes
                  #(conj % {:_id        _id
                            :name       name
                            :parent     nil
                            :node-level 0
                            :children   []}))))
  ([nodes {:keys [name _id] :as node-data} parent-id]
   (dosync (alter nodes #(add-node-recursive % parent-id node-data)))))

(defn remove-node-recursive
  "Recursive function to remove a node from a tree. Returns a vector."
  [nodes node-id]
  (if-let [node (first (filter #(= (:_id %) node-id) nodes))]
    (-> (fn [node] (when (not= (:_id node) node-id) node))
        (keep nodes)
        vec)
    (-> (fn [node]
          (update node
                  :children
                  #(remove-node-recursive % node-id)))
        (mapv nodes))))

(defn remove-node
  "Remove node from a tree structure cf/sg structure.
  Returns a new tree with the removed node."
  [nodes node-id]
  (dosync (alter nodes #(remove-node-recursive % node-id))))












