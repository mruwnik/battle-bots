(ns arena.rules-test
  (:require [clojure.test :refer :all]
            [arena.rules :refer :all]))

(defn contestant-moves [name move]
  {:name name :callback "callback" :moves [move] :score 0})

(defn check-winner [expected contestants]
  "Check whetehr the winning contestant has the provided name. If there are more than 1 winner, nil is expected."
  (let [contestants (rock-paper-scissors (map #(apply contestant-moves %) contestants))
        max-scored (apply max-key :score contestants)]
    (if (= (count (filter #(= (:score %) (:score max-scored)) contestants)) 1)
      (is (= expected (:name max-scored)))
      (is (nil? expected)))))

(deftest test-rock-paper-scissors
  (testing "2 players, one wins"
    (check-winner "test1" {"test1" :rock "test2" :scissors})
    (check-winner "test1" {"test1" :scissors "test2" :paper})
    (check-winner "test1" {"test1" :paper "test2" :rock}))

  (testing "2 players, draw"
    (check-winner nil {"test1" :rock "test2" :rock})
    (check-winner nil {"test1" :paper "test2" :paper})
    (check-winner nil {"test1" :scissors "test2" :scissors}))

  (testing "3 players, one wins"
    (check-winner "test1" {"test1" :rock "test2" :scissors "test3" :scissors})
    (check-winner "test1" {"test1" :scissors "test2" :paper "test3" :paper})
    (check-winner "test1" {"test1" :paper "test2" :rock "test3" :rock}))

  (testing "3 players, draw"
    (check-winner nil {"test1" :rock "test2" :rock "test3" :rock})
    (check-winner nil {"test1" :paper "test2" :paper "test3" :paper})
    (check-winner nil {"test1" :scissors "test2" :scissors "test3" :scissors})
    (check-winner nil {"test1" :rock "test2" :paper "test3" :scissors})))

(deftest test-rock-paper-parser
  (testing "check whether results get properly parsed"
    (is (= :rock ((get-in available-games [:rock-paper-scissors :parser]) "rock")))
    (is (= :rock ((get-in available-games [:rock-paper-scissors :parser]) "ROck")))
    (is (= :paper ((get-in available-games [:rock-paper-scissors :parser]) "paPER")))
    (is (= :paper ((get-in available-games [:rock-paper-scissors :parser]) "paper")))
    (is (= :scissors ((get-in available-games [:rock-paper-scissors :parser]) "sCisSOrs")))
    (is (= :scissors ((get-in available-games [:rock-paper-scissors :parser]) "scissors"))))

  (testing "Check whether invalid moves return nil"
    (is (nil? ((get-in available-games [:rock-paper-scissors :parser]) " paPER ")))
    (is (nil? ((get-in available-games [:rock-paper-scissors :parser]) " pa ")))
    (is (nil? ((get-in available-games [:rock-paper-scissors :parser]) "papper")))
    (is (nil? ((get-in available-games [:rock-paper-scissors :parser]) "paper rock")))
    (is (nil? ((get-in available-games [:rock-paper-scissors :parser])  "")))
    (is (nil? ((get-in available-games [:rock-paper-scissors :parser]) "asdasdasd")))))


(deftest test-dilemma-parser
  (testing "check whether results get properly parsed"
    (is (= :help ((get-in available-games [:iterated-prisoners-dilemma :parser]) "help")))
    (is (= :help ((get-in available-games [:iterated-prisoners-dilemma :parser]) "HeLp")))
    (is (= :cheat ((get-in available-games [:iterated-prisoners-dilemma :parser]) "cheat")))
    (is (= :cheat ((get-in available-games [:iterated-prisoners-dilemma :parser]) "CHeAT"))))

  (testing "Check whether invalid moves return nil"
    (is (nil? ((get-in available-games [:iterated-prisoners-dilemma :parser]) " help ")))
    (is (nil? ((get-in available-games [:iterated-prisoners-dilemma :parser]) " he ")))
    (is (nil? ((get-in available-games [:iterated-prisoners-dilemma :parser]) "phelpper")))
    (is (nil? ((get-in available-games [:iterated-prisoners-dilemma :parser]) "cheat rock")))
    (is (nil? ((get-in available-games [:iterated-prisoners-dilemma :parser])  "")))
    (is (nil? ((get-in available-games [:iterated-prisoners-dilemma :parser]) "asdasdasd")))))


(defn check-dilemma-scores [p1 p2 expected]
  (is (= (map :score (prisoner-dilemma [p1 p2])) expected)))

(deftest test-iterated-dilemma
  (testing "Check whether the basic rules score correctly."
    (check-dilemma-scores {:moves [:help] :score 0} {:moves [:help] :score 0} [3 3])
    (check-dilemma-scores {:moves [:help] :score 0} {:moves [:cheat] :score 0} [0 5])
    (check-dilemma-scores {:moves [:cheat] :score 0} {:moves [:help] :score 0} [5 0])
    (check-dilemma-scores {:moves [:cheat] :score 0} {:moves [:cheat] :score 0} [1 1]))

  (testing "Check whether edge cases are correctly handled."
    (check-dilemma-scores {:moves [nil] :score 0} {:moves [:help] :score 0} [0 3])
    (check-dilemma-scores {:moves [nil] :score 0} {:moves [:cheat] :score 0} [0 3])

    (check-dilemma-scores {:moves [:help] :score 0} {:moves [nil] :score 0} [3 0])
    (check-dilemma-scores {:moves [:cheat] :score 0} {:moves [nil] :score 0} [3 0])

    (check-dilemma-scores {:moves [nil] :score 0} {:moves [nil] :score 0} [0 0]))

  (testing "Check whether previous scores are updated."
    (check-dilemma-scores {:moves [:help] :score 5} {:moves [:help] :score 6} [8 9])
    (check-dilemma-scores {:moves [:help] :score 10} {:moves [:cheat] :score 2} [10 7])
    (check-dilemma-scores {:moves [:cheat] :score 7} {:moves [:help] :score 1} [12 1])
    (check-dilemma-scores {:moves [:cheat] :score 2} {:moves [:cheat] :score 3} [3 4])))
