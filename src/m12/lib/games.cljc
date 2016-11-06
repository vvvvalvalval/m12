(ns m12.lib.games)

(defn non-repeating [next-problem]
  (fn [config problem]
    (->> problem (iterate #(next-problem config %)) (remove #{problem}) first)))

(defn simple-random-game
  "Helper for making a game which has 1 answer, and which generates random problems without memory (just non-repeating)."
  [{:as game
    :keys [generate-problem
           get-the-answer]}]
  {:initial-problem
   (fn [config]
     (generate-problem config))
   :next-problem
   (non-repeating (fn [config _] (generate-problem config)))
   :answer-correct?
   (fn [problem submitted-answer]
     (= submitted-answer (get-the-answer problem)))})


