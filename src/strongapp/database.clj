(ns strongapp.database
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.specs :as specs]
   [clojure.set :as cljset]))

(specs/instrument) ; instruments all next.jdbc API functions for gucci error messages

(def db {:dbtype "postgresql" :dbname "strongdb" :user "strongapp" :password "foobar"})
(def ds (jdbc/get-datasource db))

(defn insert
  [data]
  (jdbc/execute! ds ["
    insert into test(id,data)
      values('5','hello strongapp')
  "]))


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
