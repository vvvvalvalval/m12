(ns m12.lib.games.components
  (:require [rum.core :as rum]
            [sablono.core :as sab :include-macros true]

            [m12.lib.games :as games]
            [m12.utils :as u])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defn submit-answer [game-state answ]
  (update game-state :game/state assoc :submitted-answer answ))

(defn submit-answer!
  [state-atom answ]
  (swap! state-atom submit-answer answ))

(defn next-pb [game-state next-problem config]
  (-> game-state
    (update :game/problem #(next-problem config %))
    (assoc :game/state {:answered nil})))

(defn next-pb!
  [next-problem state-atom config]
  (swap! state-atom next-pb next-problem config))

(defc <game-manager>
  < rum/static rum/reactive
  [state-atom
   {:as game, :keys [next-problem answer-correct?]}
   config
   <game-view>]
  (let [{problem :game/problem
         {:keys [submitted-answer]} :game/state} (rum/react state-atom)
        local-state-atom (rum/cursor state-atom :game/local-state)
        correct? (when submitted-answer
                   (answer-correct? problem submitted-answer))]
    (<game-view>
      local-state-atom
      problem
      submitted-answer
      correct?
      (u/pfn submit-answer! state-atom)
      (u/pfn next-pb! next-problem state-atom config)
      )))

(defn init-game [game config]
  {:game/problem ((:initial-problem game) config)
   :game/state {:submitted-answer nil}})

(defc <game-in-rlatom>
  < rum/static
  [game rl-key config <game-view>]
  (let [state-atom (u/rlatom rl-key #(init-game game config))]
    (<game-manager> state-atom game config <game-view>)))
