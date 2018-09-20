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

(defc <G2-view> < rum/static
  [{:as pb, :keys [s h]} answered
   correct? submit! next!
   show-hint? toggle-hint!]
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
   [:div.text-center
    [:p
     (when show-hint?
       [:span
        [:strong "Hint: "]
        (utk/<height> s) " + " [:span {:style {:font-style "italic"}} (utk/<height> (- h s))]
        " = "
        (utk/<height> h)])
     [:button.btn.btn-link
      {:on-click
       (fn [_]
         (toggle-hint!))}
      [:small (if show-hint? "Hide hint" "Show hint")]]]]
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

(defn- toggle-show-hint!
  [local-state-atom]
  (swap! local-state-atom update :game/show-hint? not))

(defc <G2> < rum/static rum/reactive
  [local-state-atom
   pb answered
   correct? submit! next!]
  (let [{show-hint? :game/show-hint?} (rum/react local-state-atom)
        toggle-hint! (u/pfn toggle-show-hint! local-state-atom)]
    (<G2-view>
      pb answered
      correct? submit! next!
      show-hint? toggle-hint!)))

(defcard <G2>-failed
  (let [s (nth (gtr/standard-guitar-strings) 3)
        h (+ s 7)]
    (<G2> (atom nil) {:s s :h h}
      (+ h 1) false #(.log js/console "submitted:" %) #(.log js/console "next!"))))

(defcard <G2>-success
  (let [s (nth (gtr/standard-guitar-strings) 3)
        h (+ s 7)]
    (<G2> (atom nil) {:s s :h h}
      h true #(.log js/console "submitted:" %) #(.log js/console "next!"))))
