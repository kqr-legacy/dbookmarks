(ns bookmarks.models
  (:require [clojure.java.io :as io]
            [korma.db :refer [defdb postgres]]
            [korma.core :refer :all]
            [cemerick.friend.credentials :refer [hash-bcrypt]]))


(defdb db
  (let [db-uri (clojure.string/trim-newline (slurp (io/resource "database.cfg")))
        param-values (re-seq #"[?&]([^=]+)=([^&]+)" db-uri)
        db-params (into {} (for [[_ param value] param-values]
                             (hash-map (keyword param) value)))
        [_ host port dbname] (re-find #"jdbc:postgresql://([^:]+):([0-9]+)/([^?]+)" db-uri)]
    (postgres (conj {:host host
                     :port port
                     :db dbname}
                    db-params))))

(declare users bookmarks)

(defentity users
  (pk :id)
  (table :users)
  (database db)
  (entity-fields :id :username)
  (has-many bookmarks))

(defentity bookmarks
  (pk :id)
  (table :bookmarks)
  (database db)
  (entity-fields :id :url :tags)
  (belongs-to users))



(defn users-add
  "Creates a new user in the database with the given username and
   password."
  [username password]
  
  (insert users
    (values {:username username
             :password (hash-bcrypt password)})))


(defn users-get
  "Returns a user with the given username from the database if it exists,
   otherwise nil."
  [username]

  (first (select users
     (fields :password)
     (where {:username username}))))


(defn bookmarks-add
  "Adds a bookmark to the table of bookmarks!"
  [user url tags]
  
  (insert bookmarks
    (values {:users_id (:id user)
             :url url
             :tags (if (nil? tags) "" tags)})))

(defn bookmarks-get
  "Gets a list of bookmarks belonging to the user"
  [user query]

   (select bookmarks
      (where {:users_id (:id user)})
      (order :pagerank :DESC)))


