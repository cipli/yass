(ns yass.rest)

(require '[clojure.string :as string])

;; let's check the structure of the arguments
(clojure.spec/check-asserts true)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 1. Setting up your API namespace ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(require '[levanzo.namespaces :as lns]
         '[clojure.test :refer [is]])

;; base URL where the API will be served
(def base (or (System/getenv "BASE_URL") "http://nuggeting.app:8080/"))

;; registering the namespace for our vocabulary at /vocab#
(lns/define-rdf-ns vocab (str base "vocab#"))

;; registering schema org vocabulary
(lns/define-rdf-ns sorg "http://schema.org/")

;; tests
(is (= "http://nuggetin.app:8080/vocab#Test") (vocab "Test"))
(is (= "http://schema.org/Person") (sorg "Person"))
(is (= "http://schema.org/Person") (lns/resolve "sorg:Person"))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 2. Describing you API            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(require '[levanzo.hydra :as hydra])
(require '[levanzo.xsd :as xsd])

;; declaring properties
(def sorg-name (hydra/property {::hydra/id (sorg "name")
                                          ::hydra/title "name"
                                          ::hydra/description "The name of the item."
                                          ::hydra/range xsd/string}))

(def sorg-description (hydra/property {::hydra/id (sorg "description")
                                       ::hydra/title "description"
                                       ::hydra/description "A description of the item."
                                       ::hydra/range xsd/string}))

(def sorg-DateTime (hydra/property {::hydra/id (sorg "DateTime")
                                    ::hydra/title "DateTime"
                                    ::hydra/description "A combination of date and time of day in the form [-]CCYY-MM-DDThh:mm:ss[Z|(+|-)hh:mm] (see Chapter 5.4 of ISO 8601)."
                                    ::hydra/range xsd/date-time}))

(def sorg-URL (hydra/property {::hydra/id (sorg "URL")
                                    ::hydra/title "URL"
                                    ::hydra/description "Data type: URL."
                                    ::hydra/range xsd/string}))

(def vocab-Nugget (hydra/class {::hydra/id (vocab "Nugget")
                               ::hydra/title "Nugget"
                               ::hydra/description "A nugget is a note that we want to save and refer to later."
                               ::hydra/supported-properties
                               [(hydra/supported-property
                                 {::hydra/property sorg-name
                                  ::hydra/required true})
                                (hydra/supported-property
                                 {::hydra/property sorg-description})
                                (hydra/supported-property
                                 {::hydra/property sorg-DateTime})
                                (hydra/supported-property
                                 {::hydra/property sorg-URL})]
                               ::hydra/operations
                               [(hydra/get-operation {::hydra/returns (hydra/id vocab-Nugget)})
                                (hydra/delete-operation {})]}))

;;(clojure.pprint/pprint (hydra/->jsonld vocab-Nugget))
;;(:operations sorg-Person)
;; Working with payloads

(require '[levanzo.payload :as payload])

(def nugget (payload/instance
             vocab-Nugget
             (payload/supported-property {:property sorg-name
                                          :value "EditorConfig"})
             (payload/supported-property {:property sorg-description
                                          :value "EditorConfig helps developers define and maintain consistent coding styles between different editors and IDEs."})
             (payload/supported-property {:property sorg-DateTime
                                          :value "2017-01-20T22:55:07+01:00"})
             ))
;;is (= address address-alt))

(require '[levanzo.schema :as schema])

;; valid
;;(schema/valid-instance? :read nugget {:supported-classes [vocab-Nugget]})

;; Collections

(def vocab-NuggetsCollection (hydra/collection {::hydra/id (vocab "Nuggets")
                                                ::hydra/title "Nuggets"
                                                ::hydra/description "Collection for all of our nuggets, in otherwords, its our notebook."
                                                ::hydra/member-class (hydra/id vocab-Nugget)
                                                ::hydra/is-paginated false
                                                ::hydra/operations
                                                [(hydra/get-operation {::hydra/returns (hydra/id vocab-NuggetsCollection)})
                                                 (hydra/post-operation {::hydra/expects (hydra/id vocab-Nugget)
                                                                        ::hydra/returns (hydra/id vocab-Nugget)})]}))

(def nuggets (payload/instance
             vocab-NuggetsCollection
             (payload/members [(payload/instance
                                vocab-Nugget
                                (payload/supported-property {:property sorg-name
                                                             :value "EditorConfig"})
                                (payload/supported-property {:property sorg-description
                                                             :value "EditorConfig helps developers define and maintain consistent coding styles between different editors and IDEs."})
                                (payload/supported-property {:property sorg-DateTime
                                                             :value "2017-01-20T22:55:07+01:00"}))
                               (payload/instance
                                vocab-Nugget
                                (payload/supported-property {:property sorg-name
                                                             :value "Monad papers"})
                                (payload/supported-property {:property sorg-description
                                                             :value "a collection of scientific articles about and introducing the concept of 'monads' in functional programming languages"})
                                (payload/supported-property {:property sorg-DateTime
                                                             :value "2017-01-20T22:56:26+01:00"}))])))

;(def people-instance (last (gen/sample (schema-spec/make-payload-gen :read vocab-PeopleCollection
;                                                                     {:supported-classes [sorg-Person vocab-PeopleCollection]}
;                                                                     100))))
;
;(schema/valid-instance? :read (payload/expand people-instance) {:supported-classes [vocab-PeopleCollection
;                                                                   sorg-Person]})
;
;(payload/context)
;(schema/valid-instance? :read (payload/expand (payload/compact people)) {:supported-classes [vocab-PeopleCollection
;                                                          sorg-Person]})
;
;;; Working with the context
;(payload/context {:base base
;                  :vocab (vocab)
;                  :ns ["vocab"]
;                  "id" "@id"
;                  "type" "@type"
;                  "Person" {"@id" (sorg "Person")}
;                  "name" {"@id" (sorg "name")}
;                  "email" {"@id" (sorg "email")}})
;
;;; expansion and compaction of JSON-LD documents
;(def tim (payload/instance
;          sorg-Person
;          ["@id" (str base "tim")]
;          (payload/supported-property {:property sorg-name
;                                       :value "Tim"})
;          (payload/supported-property {:property sorg-email
;                                       :value "timbl@w3.org"})))
;(clojure.pprint/pprint (-> tim payload/compact (dissoc "@context")))
;;;{"id" "tim",
;;; "type" "Person",
;;; "email" "timbl@w3.org",
;;; "name" "Tim"}
;
;(clojure.pprint/pprint (payload/expand tim))
;;; {"@id" "http://localhost:8080/tim",
;;;  "@type" ["http://schema.org/Person"],
;;;  "http://schema.org/email" [{"@value" "timbl@w3.org"}],
;
;(is (= (payload/expand tim) (-> tim payload/compact payload/expand)))
;
;;; More explicit context so Markus Hydra console will work correctly
;(payload/context {:base base
;                  :vocab (vocab)
;                  :ns ["vocab"]
;                  "Person" {"@id" (sorg "Person")}
;                  "name" {"@id" (sorg "name")}
;                  "email" {"@id" (sorg "email")}
;                  "address" {"@id" (sorg "address")}})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 3. Routes and links              ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; API definition
(def API (hydra/api {::hydra/id "nuggets"
                     ::hydra/title "Nuggets (Evernote-Clone) Example API"
                     ::hydra/description "A Hydra based RESTful Web Service"
                     ::hydra/entrypoint "/nuggets"
                     ::hydra/entrypoint-class (hydra/id vocab-NuggetsCollection)
                     ::hydra/supported-classes [vocab-NuggetsCollection
                                                vocab-Nugget]}))


(require '[levanzo.http :as http] :reload)

;; let's check the structure of the arguments to middleware
(clojure.spec/check-asserts true)

(def nuggets-db (atom {"http://nuggeting.app:8080/nuggets/1"
                       {"@id" "http://nuggeting.app:8080/nuggets/1",
                       "@type" ["http://nuggeting.app:8080/vocab#Nugget"],
                        "http://schema.org/name" [{"@value" "EditorConfig"}],
                        "http://schema.org/description" [{"@value" "EditorConfig helps developers define and maintain consistent coding styles between different editors and IDEs."}],
                        "http://schema.org/DateTime" [{"@value" "2017-01-20T22:55:07+01:00"}]}
                       "http://nuggeting.app:8080/nuggets/2"
                       {"@id" "http://nuggeting.app:8080/nuggets/2",
                        "@type" ["http://nuggeting.app:8080/vocab#Nugget"],
                        "http://schema.org/name" [{"@value" "Monad papers"}],
                        "http://schema.org/description" [{"@value" "a collection of scientific articles about and introducing the concept of 'monads' in functional programming languages"}],
                        "http://schema.org/DateTime" [{"@value" "2017-01-20T22:56:26+01:00"}],
                        "http://schema.org/URL" [{"@value" "http://homepages.inf.ed.ac.uk/wadler/topics/monads.html" }]}}))

(defn get-nuggets [args body request]
  (payload/instance
   vocab-NuggetsCollection
   (payload/id {:model vocab-NuggetsCollection
                :base base})
   (payload/members (vals @nuggets-db))))

(defn post-nugget [args body request]
  (swap! nuggets-db
         #(let [id (inc (count %))
                new-nugget (-> body
                               (merge (payload/id
                                       {:model vocab-Nugget
                                        :args {:nugget-id id}
                                        :base base})))]
            (assoc % (get new-nugget "@id") (payload/expand new-nugget)))))

(defn get-nugget [args body request]
  (let [nugget (get @nuggets-db
                    (payload/link-for {:model vocab-Nugget
                                       :args args
                                       :base base}))]
    (or nugget {:status 404 :body "Cannot find resource"})))

(defn delete-nugget [args body request]
  (swap! nuggets-db #(dissoc % (payload/link-for {:model vocab-Nugget
                                                 :args args
                                                 :base base}))))

(require '[levanzo.routing :as routing])

(def api-routes (routing/api-routes {:path ["nuggets"]
                                     :model vocab-NuggetsCollection
                                     :handlers {:get get-nuggets
                                                :post post-nugget}
                                     :nested [{:path ["/" :nugget-id]
                                               :model vocab-Nugget
                                               :handlers {:get get-nugget
                                                          :delete delete-nugget}}]}))

(def api-handler (http/middleware {:api API
                                   :mount-path "/"
                                   :routes api-routes
                                   :documentation-path "/vocab"}))

(taoensso.timbre/set-level! :debug)
(http/set-debug-errors! true)
