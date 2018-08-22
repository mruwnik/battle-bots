(ns arena.game
  (:require [clojure.string :as string]
            [clojure.data.json :as json]
            [clojure.math.combinatorics :as combo]
            [arena.rules :as rules]
            [clj-http.client :as client]))


(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn contestant [name callback]
  {:name name :callback callback :id (uuid) :moves [] :score 0})

;; Arena specific stuff
(def ^:private waiting (atom {}))
(def ^:private started (atom {}))

(defn waiting-arenas [] @waiting)
(defn started-arenas [] @started)

(defn new-arena [name game rounds]
  "Add a new arena for the given game and number of rounds."
  (let [new-id (uuid)]
    (swap! waiting #(assoc % new-id {:name name :game game :rounds rounds :contestants {}}))
    new-id))

(defn remove-arena [arena-id]
  "Delete the given arena."
  (swap! waiting #(dissoc % arena-id))
  (swap! started #(dissoc % arena-id)))

(defn get-arena [desired]
  "Return the given arena, or a random one if none provided."
  (if desired
    (get @waiting desired)
    (rand-nth (keys @waiting))))

(defn add-contestant [arena-id contestant]
  "Add the given contestant to the given arena."
  (let [arena (get-arena arena-id)]
    (swap! waiting #(assoc-in % [arena-id :contestants (:id contestant)] contestant))
    {:rounds (:rounds arena) :arena arena :contestant-id (:id contestant)}))


;; Single game stuff
(defn ask-for-move [contestant state]
  "Send a request to the given contestant for thier next move."
  (try
    (:body (client/post (:callback contestant)
                        {:body (json/write-str {:state state :you (:id contestant)})
                         :content-type :json
                         :accept :json}
                        ))
  (catch clojure.lang.ExceptionInfo e)
  (catch java.net.MalformedURLException e)))


(defn contestant-move [prev-state parser contestant]
  "Update the given contestant with their newest move."
  (update contestant :moves
             #(conj % (parser (ask-for-move contestant prev-state)))))

(defn prev-state [contestant]
  "Get the last move of the given contestant."
  {:id (:id contestant)
   :name (:name contestant)
   :score (:score contestant)
   :move (last (:moves contestant))})

(defn round [parser update-winner contestants]
  "A single round of the game.
parser - how to interpret whatever the contestants return
update-winner - the rules on who won - this should return the contestants with updated scores."
  (let [state (map prev-state contestants)]
    (update-winner (map #(contestant-move state parser %) contestants))))


(defn n-rounds [rounds round-func contentants]
  (if (<= rounds 0)
    contentants (n-rounds (dec rounds) round-func (round-func contentants))))


(defn game [contestants rounds rules]
  "Run a game with the given parameters."
  (map
   (partial n-rounds rounds (partial round (:parser rules) (:updater rules)))
   (cond
     (= :pairs (:type rules)) (combo/selections contestants 2)
     (= :battle-royale (:type rules)) [contestants]
     :else [contestants])))

(defn start [arena-id]
  "Run the arena with the given id."
  (when (contains? @waiting arena-id)
    (let [arena (get @waiting arena-id)]
      (swap! waiting #(dissoc % arena-id))
      (swap! started #(assoc % arena-id
                             (assoc-in arena [:results]
                                       (game (vals (:contestants arena)) (:rounds arena) (rules/game-rules arena))))))))
