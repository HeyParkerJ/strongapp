(ns strongapp.database
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.set :as cljset]))


(defn sqlQuery
  []
  (jdbc/query "postgresql://strongapp:foobar@localhost:5432/strongdb"
             ["SELECT * from test"]))

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
  ;; don't use, not idiomatic (clojure.set/join datemap data)
  (def newdata (reduce (fn [acc m] (assoc acc (:date m) (:id m))) {} datemap))
  (def moredata (into #{} (map #(assoc % :id (newdata (:date %)))) data))

  (for [d moredata] (cljset/rename-keys d {:id :exercise-id}))
  )

;;(defn pooled-db
;;  []
;;  (pg/pool :host "localhost" :user "strongapp" :dbname "strongdb" :password "foobar"))

;;(jdbc/query pooled-db ["SELECT '* from test'"])

;;(pg/close! pooled-db)
