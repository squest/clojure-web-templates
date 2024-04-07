(ns app.content.view-skill
  (:require
    [reagent.core :as r]
    ["@mui/icons-material" :as icon]
    ["@mui/material" :as mui]
    ["react-beautiful-dnd" :as dnd]))


(defonce skills (r/atom ["Skill 1" "Skill 2" "Skill 3" "Skill 4"]))

(defn remove-at [v i]
  (into (subvec v 0 i) (subvec v (inc i))))

(defn insert-at [v i x]
  (into (into (subvec v 0 i) [x]) (subvec v i)))

(defn on-drag-end [{:keys [source destination]}]
  (when destination
    (let [src-idx (:index source)
          dest-idx (:index destination)]
      (swap! skills #(-> %
                         (remove-at src-idx)
                         (insert-at dest-idx (nth @skills src-idx)))))))

(defn skill-item [idx skill]
  [:> dnd/Draggable {:key idx :draggableId (str "skill-" idx) :index idx}
   (fn [{:keys [draggableProps dragHandleProps innerRef]}]
     [:> mui/ListItem (assoc draggableProps :ref innerRef)
      [:> mui/ListItemIcon
       [:> icon/Menu]]
      [:> mui/ListItemText {:primary skill}]])])

(defn skill-list []
  [:> dnd/Droppable {:droppableId "skillList"}
   (fn [{:keys [droppableProps innerRef placeholder]}]
     [:> mui/List (assoc droppableProps :ref innerRef)
      (doall (map-indexed skill-item @skills))
      [:div {:ref placeholder}]])])

(defn reorder-skills-dialog []
  (let [open? (r/atom false)]
    (fn []
      [:<>
       [:> mui/Button {:variant "contained" :color "primary" :on-click #(reset! open? true)} "Reorder Skills"]
       [:> mui/Dialog {:open @open? :on-close #(reset! open? false) :maxWidth "sm" :fullWidth true}
        [:> mui/DialogTitle "Reorder Skills"]
        [:> dnd/DragDropContext {:onDragEnd on-drag-end}
         [skill-list]]
        [:> mui/DialogActions
         [:> mui/Button {:color "primary" :on-click #(reset! open? false)} "Done"]]]])))