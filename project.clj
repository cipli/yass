(defproject yass "0.1.0-SNAPSHOT"
  :description "Yet Another Software Solution for App Prototyping"
  :url "http://github.com/cipli/yass"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/tools.cli "0.3.5"]
                 [levanzo "0.2.0"]
                 [org.clojure/test.check "0.9.0" :scope "test"]]
  :main ^:skip-aot yass.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
