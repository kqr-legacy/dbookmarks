(ns bookmarks.views
  (:gen-class)
  (:require [clojure.string                 :refer [join]]
            [compojure.core                 :refer [defroutes GET PUT DELETE POST]]
            [compojure.handler              :refer [site]]
            [cemerick.friend                :refer [authenticate current-authentication wrap-authorize]]
            [cemerick.friend.workflows      :refer [http-basic http-basic-deny]]
            [cemerick.friend.credentials    :refer [bcrypt-credential-fn]]
            [liberator.core                 :refer [resource defresource]]
            [bookmarks.models :as models]))

(defn universal-authorized?
  "When given a liberator context, figures out if the user is authenticated
   and then returns the user information map if it is!"
  [ctx]
  
  (when-some [auths (get-in ctx [:request :session :cemerick.friend/identity])]
    {:user (get-in auths [:authentications (:current auths)])}))


(defresource get-bookmarks []
  :authorized? universal-authorized?
  :allowed-methods [:post]
  :available-media-types ["text/plain"]
  :new? false
  :respond-with-entity? true
  :handle-ok (fn [ctx]
    ; TODO: Check for a query and do a search if present
    (join "\n"
      (for [{:keys [id url tags]} (models/bookmarks-get (:user ctx) "")]
        (join " " [id url tags])))))


(defresource add-bookmark []
  :authorized? universal-authorized?
  :allowed-methods [:post]
  :available-media-types ["text/plain"]
  :processable? (fn [ctx]
    (contains? (get-in ctx [:request :form-params]) "url"))
  :post! (fn [ctx]
    (let [{url "url" tags "tags"} (get-in ctx [:request :form-params])]
      (models/bookmarks-add (:user ctx) url tags))))




(defroutes home-routes
  ; Get a list of your bookmarks (possibly matching the POSTed query)
  (POST   "/get" []   (get-bookmarks))

  ; Submit a new bookmark in your name (must specify URL and tags)
  (POST   "/new" []   (add-bookmark))

  ; Delete one of your bookmarks
  (DELETE "/:id" [id] nil)

  ; ACK one of your bookmarks
  (POST   "/:id" [id] nil))


(def app
  (-> home-routes
      (authenticate
        {:unauthenticated-handler (partial http-basic-deny "dbookmarks")
         :workflows [(http-basic
                        :realm "dbookmarks"
                        :credential-fn (partial bcrypt-credential-fn models/users-get))]})
      site))

