(ns app.backsite.commons.content-processor
  (:require [app.utils :refer :all]
            [clojure.string :as cs]
            [clojure.edn :as edn]))

(declare process-one-soal process-tempseq process-note)

(defn validate-json
  "Validate the content input"
  [content-type substance]
  (if (= "note" content-type)
    (and (= #{:text} (disj (set (keys substance)) [:_id]))
         (string? (:text substance)))
    (and (= #{:soals} (disj (set (keys substance)) [:_id]))
         (vector? (:soals substance))
         (every? #(= #{:text-soal :options :explanation} (set (keys %))) (:soals substance))
         (every? #(string? (:text-soal %)) (:soals substance))
         (every? #(string? (:explanation %)) (:soals substance))
         (every? #(vector? (:options %)) (:soals substance))
         (every? (fn [s] (every? #(integer? (nth % 0)) (:options s))) (:soals substance))
         (every? (fn [s] (every? #(boolean? (nth % 1)) (:options s))) (:soals substance))
         (every? (fn [s] (every? #(string? (nth % 2)) (:options s))) (:soals substance)))))

(defn edn->raw
  "Transform from edn format of content to raw format"
  [content-type edn-data]
  (if (= "note" content-type)
    (:text edn-data)
    (let [f-opt (fn [opt]
                  (str (nth opt 1) "::" (nth opt 2) "  <=> \n"))
          f-soal (fn [soal]
                   (loop [[x & xs] (:options soal)
                          res (str (:text-soal soal)
                                   "\n\n ==explanation== \n\n"
                                   (:explanation soal)
                                   "\n\n ==options== \n\n")]
                     (if (nil? x)
                       res
                       (recur xs (str res (f-opt x))))))]
      (loop [[x & xs] (rest (:soals edn-data))
             res (f-soal (first (:soals edn-data)))]
        (if (nil? x)
          res
          (recur xs (str res "\n\n ==sepa== \n\n" (f-soal x))))))))

(defn add-options
  "Add options to the soal for a template or sequence"
  [substance]
  (let [f-opt (fn [choice]
                [(:idx choice) (:correct choice) (:option-text choice)])]
    {:soals (->> (:soals substance)
                 (mapv #(assoc % :options (mapv f-opt (:choices %)))))}))

(defn process-new-content
  "Just for checking the content data"
  [content-data]
  (let [format (:format content-data)
        content-type (get content-data :content-type)
        id (uuid)
        substance-id (uuid)]
    (info "Masuk ke dalem process new content")
    (when (or (and (= "text" format) (string? (:raw content-data)))
              (and (= "json" format)
                   (map? (:substance content-data))
                   (validate-json content-type (:substance content-data))))
      (info "Passed the validation")
      (when-let [result (try (if (= "text" format)
                               (-> content-data
                                   (merge {:_id          id
                                           :substance-id substance-id})
                                   (assoc :substance
                                          (merge {:_id substance-id}
                                                 (if (= "note" content-type)
                                                   (process-note (:raw content-data))
                                                   (process-tempseq (:raw content-data)))))
                                   (dissoc :format))
                               (-> content-data
                                   (merge
                                     {:_id          id
                                      :substance-id substance-id
                                      :raw          (edn->raw content-type (:substance content-data))})
                                   (assoc-in [:substance :_id] substance-id)
                                   (dissoc :format)))
                             (catch Exception e
                               (info (str e))))]
        result))))

(defn process-edit-content
  "Another checking for edit content, but the ids already available"
  [content-data]
  (let [format (:format content-data)
        content-type (get content-data :content-type)]
    (info "Masuk ke dalem process edit content")
    (when (or (and (= "text" format) (string? (:raw content-data)))
              (and (= "json" format)
                   (map? (:substance content-data))
                   (validate-json content-type (:substance content-data))))
      (info "Passed the validation")
      (let [result (if (= "text" format)
                     (-> content-data
                         (assoc :substance
                                (if (= "note" content-type)
                                  (process-note (:raw content-data))
                                  (process-tempseq (:raw content-data))))
                         (dissoc :format))
                     (-> (dissoc content-data :format)
                         (assoc :raw (edn->raw content-type (:substance content-data)))))]
        result))))

(defn process-note
  [raw-note]
  {:text (cs/trim raw-note)})

(defn process-tempseq
  "This function will read the typical template/sequence and transform it into edn data"
  [problem-text]
  (let [soals (cs/split problem-text #"==sepa==")
        processed-soals (mapv process-one-soal soals)]
    {:soals (vec processed-soals)}))

(defn- process-one-option
  [ikey option]
  (let [[anskey text] (cs/split option #"::")]
    [ikey (edn/read-string anskey) (cs/trim text)]))

(defn- process-one-soal
  [soal-string]
  (let [[text options] (cs/split soal-string #"==options==")
        [text-soal explanation] (cs/split text #"==explanation==")
        the-options (-> (cs/trim options)
                        (cs/split #"<=>"))
        processed-options (map-indexed process-one-option the-options)]
    {:text-soal   (cs/trim text-soal)
     :explanation (cs/trim explanation)
     :options     (vec processed-options)
     :choices     (-> #(do {:idx         (nth % 0)
                            :correct     (nth % 1)
                            :option-text (nth % 2)})
                      (mapv processed-options))}))
