(ns strongapp.database
  (:require
   [honeysql.core :as sql]
   [honeysql.helpers :as helpers]
   [next.jdbc :as jdbc]
   [next.jdbc.specs :as specs]
   [clojure.string :as cljstring]
   [clojure.set :as cljset]))

(specs/instrument) ; instruments all next.jdbc API functions for gucci error messages

(def db {:dbtype "postgresql"
         :dbname "strongdb"
         :user (System/getenv "STRONGAPP_DB_USER")
         :password (System/getenv "STRONGAPP_DB_PASS")})
(def ds (jdbc/get-datasource db))

(def keysToParseToInteger [:rpe :reps :seconds :set-order :exercise-id :distance])

(defn insert
  [data]
  (jdbc/execute! ds (-> (helpers/insert-into :test-exercises)
                        (helpers/values data)
                        sql/format)))

;; This actually just puts all the data into the database. Need to rename.
(defn get-all-data
  [args]
  ; TODO - check what args are
  (jdbc/execute! ds (-> (helpers/select :*)
                        (helpers/from :test_exercises)
                        (sql/format))))

(defn parse-number
  "Reads a number from a string. Returns nil if not a number."
  [s]
  (if (and (not (cljstring/blank? s)) (re-find #"^-?\d+\.?\d*$" s))
    (read-string s)))

(defn update-vals [map vals f]
  (reduce #(update-in % [%2] f) map vals))

(defn updateValues
  [data]
  (update-vals data
               [:rpe :reps :seconds :set-order :exercise-id :distance :weight]
               parse-number))


;; TODO - Convert to -> syntax
(defn groom-data
  [data]
  ;; Add unique workout ids to the data based on unique date strings
  (def dates (for [d data
                   :let [y (get d :date)]]
               y))
  (def datemap (map #(hash-map :id %2 :date %1) (set dates) (iterate inc 0)) )
  (def newdata (reduce (fn [acc m] (assoc acc (:date m) (:id m))) {} datemap))
  (def moredata (into #{} (map #(assoc % :id (newdata (:date %)))) data))
  (def finaldata (map updateValues moredata))

  ;; TODO - Do this earlier
  (for [d finaldata] (cljset/rename-keys d {:id :exercise-id})))
