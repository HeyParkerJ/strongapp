(ns strongapp.database
  (:require
   [clj-postgresql.core :as pg]
   [clojure.java.jdbc :as sql]))


(defn sqlQuery
  []
  (sql/query "postgresql://@localhost:5432/strongdb"
             ["SELECT * from test"]))

(defn get-test
  []
  (sqlQuery))

(defn insert
  [spec]
  (sql/insert! spec
               :test {:data "Hello World"}))

;;(defn pooled-db
;;  []
;;  (pg/pool :host "localhost" :user "strongapp" :dbname "strongdb" :password "foobar"))

;;(sql/query pooled-db ["SELECT '* from test'"])

;;(pg/close! pooled-db)
