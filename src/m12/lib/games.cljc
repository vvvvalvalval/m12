(ns m12.lib.games)

(defn non-repeating [next-problem]
  (fn [problem]
    (->> problem (iterate next-problem) (remove #{problem}) first)))

;; TODO maybe add a notion of a game configuration (Val, 03 Nov 2016)
(defn simple-random-game
  "Helper for making a game which has 1 answer, and which generates random problems without memory (just non-repeating)."
  [{:as game
    :keys [generate-problem
           get-the-answer]}]
  {:initial-problem
   generate-problem
   :next-problem
   (non-repeating (fn [_] (generate-problem)))
   :answer-correct?
   (fn [problem submitted-answer]
     (= submitted-answer (get-the-answer problem)))})


