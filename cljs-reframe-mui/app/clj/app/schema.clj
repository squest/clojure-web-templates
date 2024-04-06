(ns app.schema
  (:require [schema.core :as s]))

;; CONTENT LAYER

(def content-types #{"note" "template" "sequence" "common-text"})
(def tones #{"formal" "informal"})
(def selection-types #{"single" "multiple"})

;; MongoDB collections are all in plural forms except content-types

(def Content
  "Content is the atomic content that can be part of a content folder,
  content is just a reference to the actual content which can be any of the
  following: note, template, sequence. The actual content of this content is
  stored in the note/template/sequence collection."
  ;; mongo collection "contents"
  {:_id                  s/Str
   ;; _id is the id of the actual content
   :title                s/Str
   :substance-id         s/Str
   ;; substance-id is the id of the substance of this content
   :folder               s/Str
   :raw                  s/Str
   :author-name          s/Str
   :author-username      s/Str
   :date-created         s/Str
   :last-edited          s/Str
   :edit-author-name     s/Str
   :edit-author-username s/Str
   :tone                 (s/pred tones)
   :content-type         (s/pred content-types)
   :selection-type       (s/pred selection-types)})

(def ContentFolder
  "ContentFolder is the folder for content"
  ;; mongo collection "content-folders"
  {:_id                       s/Str
   :name                      s/Str
   :node-level                s/Int
   :parent                    (s/maybe s/Str)
   :children                  [(s/maybe s/Str)]
   :owner-username            s/Str
   (s/optional-key :contents) [(s/maybe Content)]})

(def ContentFolderTree
  "ContentFolderTree is the tree structure version of content folder"
  {:_id                       s/Str
   :name                      s/Str
   :node-level                s/Int
   :parent                    (s/maybe s/Str)
   :children                  [(s/maybe s/Any)]
   :owner-username            s/Str
   (s/optional-key :contents) [(s/maybe Content)]})

(def Note
  "Note is one of the content-type that is used to store the actual content"
  ;; mongo collection "note"
  {:_id  s/Str
   :text s/Str})

(def Option
  "Option map for template and sequence soals"
  {:idx         s/Int
   :correct     s/Bool
   :option-text s/Str})

(def Soal
  "The soal that can be part of a template or a sequence"
  {:text-soal   s/Str
   :explanation s/Str
   :choices     [Option]
   :options     [s/Any]})

;; options should be in the form of [index(int) boolean string]
;; int represent the order starting from 0
;; true/false represent the correct/incorrect answer
;; string represent the text of the answer

(def Template
  "The template that can have multiple similar soals"
  ;; mongo collection "template"
  {:_id   s/Str
   :soals [Soal]})

(def Sequence
  "The sequence is a vector of step by step soals"
  ;; mongo collection "sequence"
  {:_id   s/Str
   :soals [Soal]})

(def RelContentSkill
  "RelContentSkill is the relationship between content and skill"
  ;; mongo collection "rel-content-skill"
  {:_id           s/Str
   :content-id    s/Str
   :skill-id      s/Str
   :approval      s/Str
   :approval-date s/Str
   :approved      s/Bool})

(def SkillGroup
  "SkillGroup is the folder for skills"
  ;; mongo collection "skill-groups"
  {:_id        s/Str
   :name       s/Str
   :node-level s/Int
   :parent     (s/maybe s/Str)
   :children   [(s/maybe s/Str)]})

(def SkillGroupTree
  "SkillGroupTree is the in-memory tree of skill group"
  {:_id        s/Str
   :name       s/Str
   :node-level s/Int
   :parent     (s/maybe s/Str)
   :children   [(s/maybe SkillGroupTree)]})


(def Skill
  "The atomic level of skill that can be tested and can be part of multple skill groups"
  ;; mongo collection "skills"
  {:_id                  s/Str
   :name                 s/Str
   :description          s/Str
   :author-name          s/Str
   :author-username      s/Str
   :date-created         s/Str
   :last-edited          s/Str
   :edit-author-name     s/Str
   :edit-author-username s/Str
   :approval             s/Str
   :approval-date        s/Str
   :approved             s/Bool})

(def RelSkillGroupSkill
  "RelSkillGroupSkill is the relationship between skill group and skill"
  ;; mongo collection "rel-skill-group-skill"
  {:_id      s/Str
   :sg-id    s/Str
   :skill-id s/Str})

(def RelJobSkillGroup
  "JobSG is the one of the sg required for the job role, including associated fields"
  ;; mongo collection "job-sgs"
  {:_id       s/Str
   :sg-id     s/Str
   :job-id    s/Str
   :weight    s/Int
   :min-level s/Int})

(def JobRole
  ;; mongo collection "job-roles"
  {:_id                  s/Str
   :name                 s/Str
   :author-name          s/Str
   :author-username      s/Str
   :edit-author-name     (s/maybe s/Str)
   :edit-author-username (s/maybe s/Str)
   :date-created         s/Str
   :last-edited          s/Str})

;; Schema for managing user and web etc

(def User
  "User is the content maker"
  ;; monggo collection "users"
  ;; role is one of #{"admin", "manager", "content-maker"}
  {:_id      s/Str
   :username s/Str
   :password s/Str
   :token    s/Str
   :role     (s/enum :admin :manager :content-maker)
   :approved s/Bool
   :name     s/Str})

;; Schema for application layer

(def SoalAnalytics
  "SoalAnalytics is the analytics for soal"
  {:_id        s/Str
   :content-id s/Str
   :correct    s/Int
   :wrong      s/Int
   :total      s/Int
   :speed      s/Int
   :accuracy   s/Int})

(def SGinfo
  "Skill group promotional info"
  {:_id          s/Str
   :sg-id        s/Str
   :learner-info s/Str
   :company-info s/Str
   :image-url    s/Str})

(def learner
  "Learner is the user that is learning the content"
  {:_id      s/Str
   :username s/Str
   :token    s/Str})

(def InstanceSoal
  "InstanceSoal is one picked soal for a template/sequence"
  {:content-id s/Str
   :choices    [Option]
   :order      s/Int})

(def SGPracticeSession
  "SGPracticeSession is the practice session for a skillgroup"
  {:_id        s/Str
   :session-id s/Str
   :learner-id s/Str
   :sg-id      s/Str
   :soals      [InstanceSoal]})

(def ContentRequest
  "ContentRequest is the request for one content"
  {:content-type s/Str
   :description  s/Str
   :order        s/Int})

(def RFC
  "RFC is a Request For Content, connected to a skill"
  ;; mongo collection rfcs
  {:_id               s/Str
   ;; name of the request for content
   :name              s/Str
   ;; username of the requester
   :requester         s/Str
   :timestamp         s/Str
   :skill-name        s/Str
   :skill-id          s/Str
   :skill-description s/Str
   ;; the folder in which the results will be stored
   :folder-id         s/Str
   :contents          [ContentRequest]
   :status            (s/enum "waiting" "done" "error")})

(def GPTContext
  "These are zenbrain contexts, gpt-contexts"
  {:_id          s/Str
   :name         s/Str
   :substance    s/Str
   :date-created s/Str
   :author       s/Str
   :last-edited  s/Str
   :last-editor  s/Str})

















