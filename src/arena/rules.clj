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

(defn rock-paper-parser [body]
  "Parse the move returned by a contestant."
  (let [body (string/lower-case (str body))]
    (when (some #{body} ["rock" "paper" "scissors"])
      (keyword body))))


(defn game-rules [arena]
  "Get the parser and rules for the given arena."
  ((:game arena)
   {:rock-paper-scissors [rock-paper-parser rock-paper-scissors]}))
