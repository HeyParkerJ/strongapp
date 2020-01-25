(ns strongapp.database
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.set :as cljset]))


(defn insert
  [spec data]
  (jdbc/insert-multi! spec :test_exercises data))

(defn groom-data
  [data]
  ;; Add unique workout ids to the data based on unique date strings
  (def dates (for [d data
                   :let [y (get d :date)]]
               y))
  (def datemap (map #(hash-map :id %2 :date %1) (set dates) (iterate inc 0)) )
  (def newdata (reduce (fn [acc m] (assoc acc (:date m) (:id m))) {} datemap))
  (def moredata (into #{} (map #(assoc % :id (newdata (:date %)))) data))

  ;; TODO - Do this earlier
  (for [d moredata] (cljset/rename-keys d {:id :exercise-id})))
