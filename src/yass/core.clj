(ns yass.core
  (:import (java.net InetAddress))
  (:gen-class))
(require '[clojure.string :as string]
         '[clojure.string :as string]
         '[clojure.tools.cli :refer [parse-opts]]
         '[yass.cmd :as cmd])

(def cli-options
  [;; First three strings describe a short-option, long-option with optional
   ;; example argument description, and a description. All three are optional
   ;; and positional.
   ["-p" "--port PORT" "Port number"
    :default 8080
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-H" "--hostname HOST" "Remote host"
    :default (InetAddress/getByName "localhost")
    ;; Specify a string to output in the default column in the options summary
    ;; if the default value's string representation is very ugly
    :default-desc "localhost"
    :parse-fn #(InetAddress/getByName %)]
   ;; If no required argument description is given, the option is assumed to
   ;; be a boolean option defaulting to nil
   [nil "--detach" "Detach from controlling process"]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    ;; If no long-option is specified, an option :id must be given
    :id :verbosity
    :default 0
    ;; Use assoc-fn to create non-idempotent options
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Yet Another Software Solution for App Prototyping"
        ""
        "Usage: yass [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  scaffold   create default directory structure for your app"
        "  edit       Add, edit or delete entries from your app"
        "  morph      Extend your app to handle more complex use cases"
        "  run        Run your app as a RESTful Web Service"
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    ;; Execute program with options
    (case (first arguments)
      "scaffold" (cmd/scaffold options)
      "edit" (cmd/edit options)
      "morph" (cmd/morph options)
      "run" (cmd/run options)
      (exit 1 (usage summary)))))
