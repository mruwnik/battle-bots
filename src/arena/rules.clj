(ns arena.rules
  (:require [clojure.string :as string]))

;; Specific game rules
(defn rock-paper-scissors [contestants]
  "Update the contestants with however many points they won in the last round."
  (let [rock (count (filter #(= :rock (last (:moves %))) contestants))
        paper (count (filter #(= :paper (last (:moves %))) contestants))
        scissors (count (filter #(= :scissors (last (:moves %))) contestants))
        winner (cond
                 (and (= rock 1) (= paper 0)) :rock
                 (and (= paper 1) (= scissors 0)) :paper
                 (and (= scissors 1) (= rock 0)) :scissors
                 :else nil)]
    (map #(if (= winner (last (:moves %))) (update-in % [:score] inc) %) contestants)))

(defn keyword-parser [allowed body]
  "Parse the provided body. If it is one of the allowed values it gets returned
 as a keyword. Otherwise nil."
  (let [body (string/lower-case (str body))]
    (when (some #{body} allowed)
      (keyword body))))

(defn prisoner-dilemma [players]
  (map #(update %1 :score + %2)
       players
       (let [[move1 move2] (map (comp last :moves) players)]
         (cond
           ; Check if anyone made a move
           (= nil move1 move2) [0 0]
           ; if one of the players didn't make a move, grant the other player a free 3 points
           (nil? move1) [0 3]
           (nil? move2) [3 0]
           ; Both players moved - grant them points
           (= move1 move2 :help) [3 3]
           (= move1 move2 :cheat) [1 1]
           (= move1 :cheat) [5 0]
           (= move2 :cheat) [0 5]))))

(def available-games
  {:rock-paper-scissors {:parser (partial keyword-parser ["rock" "paper" "scissors"]) :updater rock-paper-scissors :type :battle-royale}
   :iterated-prisoners-dilemma {:parser (partial keyword-parser ["help" "cheat"]) :updater prisoner-dilemma :type :pairs}})

(defn game-rules [arena]
  "Get the parser and rules for the given arena."
  ((:game arena) available-games))
