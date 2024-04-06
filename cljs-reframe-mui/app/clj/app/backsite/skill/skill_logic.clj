(ns app.backsite.skill.skill-logic
  (:require [app.utils :refer :all]
            [monger.collection :as mc]
            [app.content.dblogic :as lc]))

(defn sg-by-id
  "Get skill group by id"
  [db id]
  (mc/find-map-by-id (:db-1 db) "skill-groups" id))

(defn all-skills
  "Get all skills"
  [db]
  (mc/find-maps (:db-1 db) "skills"))

(defn skill-by-id
  "Get skill by id"
  [db id]
  (mc/find-map-by-id (:db-1 db) "skills" id))

(defn skills-by-sg-id
  "Get skills by skill-group id"
  [db sg-id]
  (let [sg-skills (mc/find-maps (:db-1 db) "rel-skill-group-skill" {:sg-id sg-id})]
    (if sg-skills
      (let [skill-ids (mapv :skill-id sg-skills)]
        (mc/find-maps (:db-1 db) "skills" {:_id {"$in" skill-ids}}))
      [])))

(defn skills-by-content-id
  "Get skills by content id"
  [db content-id]
  (let [content-skills (mc/find-maps (:db-1 db) "rel-content-skill" {:content-id content-id})]
    (if content-skills
      (->> (mapv :skill-id content-skills)
           (mc/find-maps (:db-1 db) "skills"))
      [])))

(defn contents-by-skill-id
  "Get all contents by skill id, both approved and unapproved"
  [db skill-id]
  (let [content-skill (mc/find-maps (:db-1 db) "rel-content-skill" {:skill-id skill-id})]
    (if content-skill
      (let [result (->> (mapv :content-id content-skill)
                        (mapv #(lc/content-by-id db %))
                        (mapv #(assoc %2 :approved (:approved %1)) content-skill))]
        (info "Result of contents-by-skill-id")
        result)
      [])))

(defn sgs-by-skill-id
  "Get skillgroups by skill id"
  [db skill-id]
  (let [sg-skills (mc/find-maps (:db-1 db) "rel-skill-group-skill" {:skill-id skill-id})]
    (if sg-skills
      (->> (mapv :sg-id sg-skills)
           (mc/find-maps (:db-1 db) "skill-groups"))
      [])))

(defn add-skill
  "Add a skill to db"
  [db skill-data user-data]
  (info "Getting into add-skill")
  (when-let [sg (mc/find-map-by-id (:db-1 db) "skill-groups" (:sg-id skill-data))]
    (info "Inside add skill and sg found")
    (let [id (uuid)
          new-skill (merge {:_id id :name (:skill-name skill-data)}
                           {:author-name     (:name user-data)
                            :description     "This is skill description"
                            :author-username (:username user-data)
                            :approved        false
                            :date-created    (now)
                            :last-edited     (now)})
          sgs-rel {:sg-id (:sg-id skill-data) :skill-id id :_id (uuid)}]
      (mc/insert (:db-1 db) "skills" new-skill)
      (mc/insert (:db-1 db) "rel-skill-group-skill" sgs-rel)
      {:status     "ok"
       :message    "Skill successfully added"
       :skill-data new-skill})))

(defn change-description
  [db skill-id description]
  (info "Getting into change-description of a skill")
  (let [skill-data (mc/find-map-by-id (:db-1 db) "skills" skill-id)]
    (if skill-data
      (let [new-skill (assoc skill-data :description description)]
        (mc/update-by-id (:db-1 db) "skills" skill-id new-skill)
        {:status     "ok"
         :message    "Skill description successfully changed"
         :skill-data new-skill})
      {:status  "error"
       :message "Skill not found"})))

(defn update-skill
  "Edit skill"
  [db new-skill-data user-data]
  (info "Getting into update-skill")
  (let [skill-id (:_id new-skill-data)
        skill (mc/find-map-by-id (:db-1 db) "skills" skill-id)
        res (when skill
              (info "Inside update-skill and skill found")
              (pres new-skill-data)
              (->> (merge skill
                          new-skill-data
                          {:last-edited          (now)
                           :edit-author-name     (:name user-data)
                           :edit-author-username (:username user-data)})
                   (mc/update (:db-1 db) "skills" {:_id skill-id})))]
    (if res
      {:status     "ok"
       :message    "Skill successfully updated"
       :skill-data res}
      {:status  "error"
       :message "Skill not found"})))

(defn- content-exist
  "Select content-ids which content actually exists in db"
  [db content-ids]
  (->> (mc/find-maps (:db-1 db) "contents" {:_id {"$in" content-ids}})
       (mapv :_id)))

(defn connect-skill-contents
  "Add contents to skill"
  [db skill-id content-ids]
  (info "Getting into connect-skill-contents")
  (let [skill (mc/find-map-by-id (:db-1 db) "skills" skill-id)
        res (when skill
              (let [exist (->> {:skill-id   skill-id
                                :content-id {"$in" content-ids}}
                               (mc/find-maps (:db-1 db) "rel-content-skill")
                               (mapv :content-id)
                               set)]
                (->> (remove exist content-ids)
                     (content-exist db)
                     (mapv #(do {:_id        (uuid)
                                 :skill-id   skill-id
                                 :content-id %
                                 :approved   true}))
                     (mc/insert-batch (:db-1 db) "rel-content-skill"))))]
    (if res
      {:status  "ok"
       :message "Contents successfully connected to a skill"}
      {:status  "error"
       :message "Skill not found"})))

(defn approval-skill-content
  [db skill-id content]
  (info "Getting into approval-skill-content")
  (let [conn (-> (mc/find-maps
                   (:db-1 db)
                   "rel-content-skill"
                   {:skill-id   skill-id
                    :content-id (:_id content)})
                 first)]
    (if-let [res (mc/update-by-id
                   (:db-1 db)
                   "rel-content-skill"
                   (:_id conn)
                   (assoc conn :approved (:approved content)))]
      (do (info "Content successfully approved/disproved")
          {:status   "ok"
           :message  "Content successfully approved/disproved"
           :contents (contents-by-skill-id db skill-id)})
      {:status  "error"
       :message "Skill not found"})))

(defn disconnect-skill-contents
  "Disconnect contents from a skill"
  [db skill-id content-ids]
  (info "Getting into disconnect-skill-contents")
  (let [res (mc/remove
              (:db-1 db)
              "rel-content-skill"
              {:skill-id   skill-id
               :content-id {"$in" content-ids}})]
    (if res
      {:status       "ok"
       :message      "Contents successfully disconnected from a skill"
       :content-list content-ids}
      {:status  "error"
       :message "Skill not found"})))

(defn delete-skill
  "Delete a skill including all connections to it"
  [db skill-id]
  (info "Getting into delete-skill")
  (mc/remove-by-id (:db-1 db) "skills" skill-id)
  (mc/remove (:db-1 db) "rel-skill-group-skill" {:skill-id skill-id})
  (mc/remove (:db-1 db) "rel-content-skill" {:skill-id skill-id})
  {:status  "ok"
   :message "Skill successfully deleted"})

(defn connect-skills-sg
  [db sg-id skill-ids]
  (info "Inside logic connect set of skills to this sg-id " sg-id)
  (let [dbcons (->> {:skill-id {"$in" skill-ids} :sg-id sg-id}
                    (mc/find-maps (:db-1 db) "rel-skill-group-skill")
                    (mapv :skill-id)
                    set)
        tbi (remove dbcons skill-ids)]
    (if (empty? tbi)
      (do (info "no skills connected")
          {:status  "error"
           :message "all skills already connected"})
      (do (info "List of skills " tbi)
          (mc/insert-batch
            (:db-1 db)
            "rel-skill-group-skill"
            (mapv #(do {:skill-id % :sg-id sg-id :_id (uuid)}) tbi))
          {:status     "ok"
           :message    "Skills successfully connected"
           :skill-list tbi
           :sg-id      sg-id}))))

(defn disconnect-skills-sg
  [db sg-id skill-ids]
  (info "Inside logic disconnect set of skills from this sg " sg-id)
  (let [res (mc/remove (:db-1 db)
                       "rel-skill-group-skill"
                       {:sg-id sg-id :skill-id {"$in" skill-ids}})]
    (if res
      (do (info "disconnection successful")
          {:status  "ok"
           :message "Skills successfully disconnected"})
      (do (info "disconnection failed")
          {:status  "error"
           :message "Skills not found"}))))
