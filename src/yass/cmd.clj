(ns yass.cmd)
(require '[org.httpkit.server :as http-kit]
         '[yass.rest :as rest])

(defn scaffold [options]
  (println "create default directory structure for your app")
  (System/exit 1))

(defn edit [options]
  (println "Add, edit or delete entries from your app")
  (System/exit 1))

(defn morph [options]
  (println "Extend your app to handle more complex use cases")
  (System/exit 1))

(defn run [options]
  (http-kit/run-server rest/api-handler {:port 8080}))
