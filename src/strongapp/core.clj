(ns strongapp.core
  (:require
   [clojure-csv.core :refer [parse-csv]]
   [strongapp.database :refer [groom-data insert]]
   [clojure.string :as s]))

(defn- keywordize-
  "takes a map, converts string keys to keyword keys
   with all lowercase and dash instead of spaces"
  [m]
  (into {}
        (for [[k v] m]
          [(keyword (s/lower-case (s/replace k #" " "-"))) v])))

(defn csv-to-map
  "parses a csv to a map
   ([csv & {:as opts}])
   passes options to clojure-csv
   converts string keys to keywords
   if ':key :keyword' is pass as extra opts.
   "
  [csv & {key :key :as opts}]
  (let [opts   (vec (reduce concat (vec opts)))
        c      (apply clojure-csv.core/parse-csv csv opts)
        output (map (partial zipmap (reverse (first c))) (map reverse (rest c)))]
    (if (= key :keyword) (map keywordize- output) output)))

(defn -main
  [& args]
  (def data (csv-to-map (slurp "resources/strong.csv") :key :keyword))
  (def finaldata (groom-data data))
  (insert "postgresql://strongapp:foobar@localhost:5432/strongdb" finaldata))
