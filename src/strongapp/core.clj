(ns strongapp.core
  (:require
   [clojure-csv.core :refer [parse-csv]]
   [strongapp.database :refer [groom-data insert get-all-data]]
   [clojure.string :as s]
   [ring.adapter.jetty :refer [run-jetty]]))

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

(defn load-csv-data
  [& args]
  (def data (csv-to-map (slurp "resources/strong.csv") :key :keyword))
  (def finaldata (groom-data data))
  (insert finaldata))


;; main
(defn -main
  [& args]
  (get-all-data nil))

;; handler
(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

;; For making the repl interaction not really suck when executing run-jetty
;; https://stackoverflow.com/questions/2706044/how-do-i-stop-jetty-server-in-clojure#2706239
(defonce server (run-jetty #'handler {:port 3000 :join? false}))
