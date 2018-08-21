(ns arena.game-test
  (:require [clojure.test :refer :all]
            [arena.game :refer :all]))

(defmacro static-uuid-testing [desc & args]
  `(testing ~desc
       (with-redefs [arena.game/uuid (fn [] "uuid")]
        ~@args)))

(deftest test-contestant
  (static-uuid-testing "Contestant creation"
     (is (= (contestant "test1" "callback") {:name "test1" :callback "callback" :id "uuid" :score 0 :moves []}))))


(deftest test-arena
  (static-uuid-testing "Check if new areans are correctly created"
     (remove-arena "uuid")
     (is (= "uuid" (new-arena "test-arena" :rock-paper-scissors 5)))
     (is (= (get (waiting-arenas) "uuid") {:name "test-arena" :rounds 5 :game :rock-paper-scissors :contestants {}})))

  (static-uuid-testing "Check if arenas are correctly removed."
     ; make sure the arena to be removed exists
     (new-arena "test-arena" :rock-paper-scissors 5)
     (is (contains? (waiting-arenas) "uuid"))
     ; remove the arena
     (remove-arena "uuid")
     (is (not (contains? (waiting-arenas) "uuid"))))

  (static-uuid-testing "Check whether getting areans works"
     (new-arena "test-arena" :rock-paper-scissors 5)
     (is (= (get-arena "uuid") {:name "test-arena" :rounds 5 :game :rock-paper-scissors :contestants {}})))

  (static-uuid-testing "Check whether adding contestants works"
     (let [contestant {:name "joe" :score 12 :id 123}]
       (add-contestant "uuid" contestant)
       (is (= (:contestants (get-arena "uuid")) {123 contestant})))))
