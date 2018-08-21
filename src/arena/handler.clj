(ns arena.handler
  (:require [clojure.string :as string]
            [arena.game :as game]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :refer [html]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [redirect]]))

(defn render-contestant [contestant]
  [:div {:class "contestant" :id (:id contestant)}
   [:span.name (str "name: " (:name contestant))]
   [:span.callback (:callback contestant)]
   (when (not-empty (:moves contestant))
     [:span.score (str (:score contestant))])
   (when (not-empty (:moves contestant))
     [:span.moves (str (:moves contestant))])
   [:br]])

(defn render-finished [id arena]
  [:div.arena
   [:span.arena=id (str "arena: " (or (not-empty (:name arena)) id))]
   (map render-contestant (:contestants arena))
   [:form {:action (str "/remove/" id) :method "POST"} [:input {:type "submit" :value "Delete"}]]])

(defn render-finished-arenas []
  [:div (map #(apply render-finished %) (game/started-arenas))])


(defn render-waiting [[id arena]]
  [:div.arena
   [:span.arena-id  (str "arena: " (or (not-empty (:name arena)) id))]
   (when (> (count (:contestants arena)) 0)
     [:form {:action (str "/start/" id) :method "POST"} [:input {:type "submit" :value "Start"}]])
   [:form {:action (str "/remove/" id) :method "POST"} [:input {:type "submit" :value "Delete"}]]
   (map (comp render-contestant second) (:contestants arena))
   [:form {:action (str "/register/" id) :method "POST"}
    [:span "Name:"] [:input {:type "text" :name "name"}]
    [:span "Callback:"] [:input {:type "text" :name "callback"}]
    [:input {:type "submit" :value "Join"}]
    ]])

(defn new-arena-form []
  [:form {:action "/new" :method "POST"}
   [:span "Name:"] [:input {:type "text" :name "name"}]
   [:span "Rounds:"] [:input {:type "text" :name "rounds" :value 10}]
   [:span "Game:"] [:select {:name "game"} (map #(vector :option {:value %} %) [:rock-paper-scissors])]
   [:input {:type "submit" :value "New arena"}]]
  )

(defn show-arenas [request]
  (html
   [:div
    [:h1 "Waiting"]
    (map render-waiting (game/waiting-arenas))
    (new-arena-form)
    [:h1 "Started"]
    (render-finished-arenas)]))

(defn register
  "Register a new contenstant."
  ([callback name] (register nil callback name))
  ([id callback name]
   (game/add-contestant id (game/contestant name callback))
   (redirect "/arenas")))

(defmacro post-redirect [endpoint func & args]
  "Add a route to the given endpoint that will call the given function with the given arguments, then redirect to the main page."
  `(POST ~endpoint ~(vec args) (do (~func ~@args) (redirect "/arenas"))))

(defroutes app-routes
  (GET "/" [] show-arenas)
  (GET "/arenas" [] show-arenas)
  (post-redirect "/start/:arena" game/start arena)
  (post-redirect "/remove/:arena" game/remove-arena arena)
  (POST "/register/:id" [id callback name :as r] (register id callback name))
  (POST "/new" [rounds game name] (do (game/new-arena name (keyword game) (Integer. rounds)) (redirect "/arenas")))
  (POST "/random_rock" [] (rand-nth ["rock" "paper" "scissors"]))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes
                 (assoc-in site-defaults [:security :anti-forgery] false)))
