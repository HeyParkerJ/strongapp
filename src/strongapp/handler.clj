(ns strongapp.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [strongapp.database :refer [ get-all-data ]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/allData" [] {:status 200
                      :content-type "application/json; charset=UTF=8"
                      :body get-all-data})
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
