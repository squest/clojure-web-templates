(ns app.db)

(defonce skill-groups
         [{:name     "Fundamental skills"
           :id       (str (random-uuid))
           :children [{:name     "Thinking skills"
                       :id       (str (random-uuid))
                       :children [{:name     "Creative thinking"
                                   :id       (str (random-uuid))
                                   :children []}
                                  {:name     "Critical thinking"
                                   :id       (str (random-uuid))
                                   :children [{:name     "Math skills"
                                               :id       (str (random-uuid))
                                               :children []}]}
                                  {:name     "Problem solving"
                                   :id       (str (random-uuid))
                                   :children []}]}]}
          {:name     "Practical skills"
           :id       (str (random-uuid))
           :children [{:name     "Business and marketing"
                       :id       (str (random-uuid))
                       :children [{:name     "marketing"
                                   :id       (str (random-uuid))
                                   :children []}]}
                      {:name     "Technology"
                       :id       (str (random-uuid))
                       :children [{:name     "Computer programming"
                                   :id       (str (random-uuid))
                                   :children []}]}]}
          {:name     "Academic skills"
           :id       (str (random-uuid))
           :children [{:name     "Mathematics and statistics"
                       :id       (str (random-uuid))
                       :children []}]}])

(def default-db
  {:name         "Template"
   :skill-groups skill-groups
   :base-url     "http://localhost:8000"})
