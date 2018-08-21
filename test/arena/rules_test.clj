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
    (is (= :rock (rock-paper-parser "rock")))
    (is (= :rock (rock-paper-parser "ROck")))
    (is (= :paper (rock-paper-parser "paPER")))
    (is (= :paper (rock-paper-parser "paper")))
    (is (= :scissors (rock-paper-parser "sCisSOrs")))
    (is (= :scissors (rock-paper-parser "scissors"))))

  (testing "Check whether invalid moves return nil"
    (is (nil? (rock-paper-parser " paPER ")))
    (is (nil? (rock-paper-parser " pa ")))
    (is (nil? (rock-paper-parser "papper")))
    (is (nil? (rock-paper-parser "paper rock")))
    (is (nil? (rock-paper-parser "")))
    (is (nil? (rock-paper-parser "asdasdasd")))))
