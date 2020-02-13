(ns strongapp.core
  (:require
   [clojure-csv.core :refer [parse-csv]]
   [compojure.core :refer :all]
   [compojure.route :as route]
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
   :body get-all-data}) ;; Body can be one of four response types - String, ISeq, File, InputStream

;; For making the repl interaction not really suck when executing run-jetty
;; https://stackoverflow.com/questions/2706044/how-do-i-stop-jetty-server-in-clojure#2706239
(defonce server (run-jetty #'handler {:port 3000 :join? false}))

(defroutes app
  (GET "/" [] "<h2>Hello World</h2>")
  (route/not-found "<h2>Page not found</h2>"))
