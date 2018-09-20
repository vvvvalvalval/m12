(ns m12.widgets.games.exG1
  "G1: find where to play a note on a guitar tab."
  (:require [m12.services.synth :as synth]
            [m12.widgets.guitar :as wgt]
            [m12.widgets.ui-toolkit :as utk]
            [rum.core :as rum]
            [m12.utils :as u]
            [m12.lib.games :as games]
            [m12.widgets.gtab :as gtab]
            [m12.lib.guitar :as gtr])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(def g1
  "G1: find where to play a cell"
  (games/basic-random-game
    {:generate-problem
     (fn [config]
       (let [s (rand-nth (gtr/standard-guitar-strings))
             j (rand-int 12)
             h (+ s j)]
         {:s s :h h}))
     :get-the-answer
     (fn [{:keys [s h]}]
       [s h])}))

;; ------------------------------------------------------------------------------
;; View

(defc <G1-cell>
  < rum/static
  [h s1 i1 j1 h1 submitted? correct? submit!]
  #_(.log js/console "Rendering cell" i1 j1)
  [:div.gtr-fretboard-cell-content.gtr-cell-blue-on-hover.find-cell-cell
   {:key "content"
    :class (cond
             (and submitted? correct?) "find-cell-cell--right"
             submitted? "find-cell-cell--wrong"
             :else "")
    :on-click (fn [_]
                (synth/play (for [h [h1 h]]
                              {:sound "acoustic" :duration 1 :height h}))
                (submit! [s1 h1]))}
   (wgt/<fretboard-column-help> i1 j1)])

(def string-height->index
  (->> (gtr/standard-guitar-strings)
    (map vector (range))
    (reduce (fn [m [i s]]
              (assoc m s i)) {})))

(defc <G1>--view < rum/static
  [{:as pb, :keys [s h]} answered
   correct? submit! next!
   show-hint? toggle-hint!]
  [:div
   [:div.row
    [:div.col-sm-3.col-md-2
     [:div {:style {;; HACK working around the fact that the strings numbers will overflow on the left
                    :padding "35px 0"
                    :paddingLeft "10px"}}
      (gtab/<gtab> {}
        {:n-strings 6 :string-heights (gtr/standard-guitar-strings)
         :length 1}
        [{::gtab/string (string-height->index s) ::gtab/x 0.5}]
        (fn [_ i]
          [:div.gtab-note
           (utk/<height> h)]))]]
    [:div.col-sm-9.col-md-10
     (wgt/<fretboard> {:class "find-cell-fretboard"}
       {:string-fn (fn [props i1 s1]
                     (cond-> props
                       (= s s1)
                       (update :style assoc
                         :borderColor "blue")))
        :row-fn (fn [props i1 s1]
                  (cond-> props
                    (= s s1)
                    (update :class #(str % " find-cell-row--selected"))))}
       (fn [_ s1 i1 j1 h1]
         (let [submitted? (when answered
                            (let [[s2 h2] answered]
                              (and (= s2 s1) (= h2 h1))))]
           (<G1-cell> h s1 i1 j1 h1 submitted? correct? submit!))))
     ]]
   [:div.text-center
    [:p
     (when show-hint?
       [:span
        [:strong "Hint: "]
        (utk/<height> h) " - " (utk/<height> s)
        " = "
        [:span {:style {:font-style "italic"}} "+" (utk/<height> (- h s))]])
     [:button.btn.btn-link
      {:on-click
       (fn [_]
         (toggle-hint!))}
      [:small (if show-hint? "Hide hint" "Show hint")]]]]
   [:div.text-center
    (cond
      correct? [:p "Well played!"
                (utk/<next-btn> {} next!)]
      (some? answered) (let [[s1 h1] answered]
                         [:p (cond
                               (not= s s1) "Dude! that's not even the right string!"
                               :else "Nope, try again.")]
                         )
      :else
      [:p " "])]
   ])

(defn- toggle-show-hint!
  [local-state-atom]
  (swap! local-state-atom update :game/show-hint? not))

(defc <G1> < rum/static rum/reactive
  [local-state-atom
   pb answered
   correct? submit! next!]
  (let [{show-hint? :game/show-hint?} (rum/react local-state-atom)
        toggle-hint! (u/pfn toggle-show-hint! local-state-atom)]
    (<G1>--view
      pb answered
      correct? submit! next!
      show-hint? toggle-hint!)))

(defcard <G1>-ex
  (<G1>
    (atom nil)
    (let [s (nth (gtr/standard-guitar-strings) 3)
              h (+ s 7)]
          {:s s :h h})
    nil nil #(do nil) #(do nil)))
