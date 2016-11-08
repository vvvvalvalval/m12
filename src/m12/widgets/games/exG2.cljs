(ns m12.widgets.games.exG2
  "G2: find the note given the cell on the fretboard"
  (:require [m12.widgets.guitar :as wgt]
            [m12.widgets.ui-toolkit :as utk]
            [rum.core :as rum]
            [m12.utils :as u]
            [m12.lib.games :as games]
            [m12.widgets.gtab :as gtab]
            [m12.lib.guitar :as gtr]
            [m12.lib.math :as math])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(def g2
  (games/basic-random-game
    {:generate-problem
     (fn [config]
       (let [s (rand-nth (gtr/standard-guitar-strings))
             j (rand-int 12)
             h (+ s j)]
         {:s s :h h}))
     :get-the-answer
     (fn [{:keys [s h]}]
       h)}))

(defn answers-range
  [{:keys [s h]}]
  (let [h0 (* 12 (math/octave-of-height s))]
    (range h0 (+ h0 (* 2 12)))))

(defc <G2>
  [{:as pb, :keys [s h]} answered
   correct? submit! next!]
  [:div
   (wgt/<fretboard> {}
     {}
     (fn [_ s1 i1 j1 h1]
       [:div.gtr-fretboard-cell-content
        {:key "content"
         :class (if (and (= s s1) (= h h1))
                  "gtr-cell-blue"
                  "")
         }
        (wgt/<fretboard-column-help> i1 j1)]
       ))
   (utk/<height-picker> {}
     {:f-props (fn [props h1]
                 (update props :class
                   #(cond-> %
                     (and (= h1 answered))
                     (str " " (if correct? "btn-success" "btn-danger")))))}
     submit!
     (answers-range pb))

   [:div.text-center
    (cond
      correct? [:p "Well played!"
                (utk/<next-btn> {} next!)]
      (some? answered) [:p "Nope, try again."]
      :else [:p " "])]
   ])

(defcard <G2>-failed
  (let [s (nth (gtr/standard-guitar-strings) 3)
        h (+ s 7)]
    (<G2> {:s s :h h}
      (+ h 1) false #(.log js/console "submitted:" %) #(.log js/console "next!"))))

(defcard <G2>-success
  (let [s (nth (gtr/standard-guitar-strings) 3)
        h (+ s 7)]
    (<G2> {:s s :h h}
      h true #(.log js/console "submitted:" %) #(.log js/console "next!"))))