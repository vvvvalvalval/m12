(ns m12.widgets.guitar
  (:require [rum.core :as rum]
            [sablono.core :as sab :include-macros true]
            [clojure.string :as str]

            [m12.utils :as u]
            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.widgets.ui-toolkit :as utk]
            [m12.lib.representations :as repr])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defn cell-left-offset
  "left offset of the n-th fretboard cell in a N-cells fretboard, as a fraction of the total width."
  [N n]
  (/ (- 1 (.pow js/Math 2 (/ (- n) 12)))
    (- 1 (.pow js/Math 2 (/ (- N) 12)))))

(defn cell-width
  "width of the n-th fretboard cell in a N-cells fretboard, as a fraction of the total width."
  [N n]
  (* (/ (- 1 (.pow js/Math 2 (/ -1 12)))
       (- 1 (.pow js/Math 2 (/ (- N) 12))))
    (.pow js/Math 2 (/ (- n) 12))))

(defc <fretboard-column-help>
  "Shows a positional tip on top of the guitar fretboard column"
  [i j]
  (when (= i 0)
    [:div.find-cell-top-cell-number.gtr-cell-fret-text
     {:key "top-cell-number"}
     (str "+" (math/to-base-12 j))]))

(defc <fretboard-cell>
  [props
   show-string-heights
   N s i j
   cell-content]
  (let [h (+ s j)]
    [:div.gtr-fretboard-cell
     (assoc props
       :class (if (= j 0) "gtr-fretboard-cell--leftmost" "")
       :style {:width (str (* 100 (cell-width N j)) "%")}
       )
     (cell-content N s i j h)
     (when (and show-string-heights (= j 0))
       [:div.gtr-cell-fret-text
        {}
        (utk/<height> h)])
     ]))

(defc <fretboard-round-mark> < rum/static
  [props N j v-offset diameter-px]
  [:div.gtr-round-wrapper
   (assoc props
     :style {:left (str (* 100 (+ (cell-left-offset N j)
                                 (/ (cell-width N j) 2))) "%")
             ;; HACK should be in LESS file (Val, 29 Oct 2016)
             :top (str (* v-offset 25) "px")
             :width (str diameter-px "px")
             :height (str diameter-px "px")
             })
   [:div.gtr-round
    {:style {:height (str diameter-px "px")
             :width (str diameter-px "px")}}]])

(defn standard-guitar-strings
  []
  (->> (str/split "24 29 32 37 3b 44" #" ")
    reverse (mapv repr/parse-height)))

(defc <fretboard>
  "Renders a guitar fretboard.
  * `opts` contains options which parameterize the fretboard.
  * `cell-content` is a function which will be called as such: (cell-content N s i j h), in which
  N is the number of displayed frets, i the index of the string (starting at 0 from the top of the tab),
  j the index of the cell, s the height of the string at 0, and h the height of the corresponding note.

  cell-content should return the content of the cell (typically using a Rum component).

  You may want the top node of that cell to have the  `gtr-fretboard-cell-content` CSS classes so that it stretches to fill the whole cell."
  [props
   {:as opts,
    :keys [N S round-diameter-px show-string-heights
           row-fn string-fn]
    :or {N 13
         S (standard-guitar-strings)
         round-diameter-px 10
         show-string-heights true
         row-fn identity
         string-fn identity}}
   cell-content]
  [:div.gtr-fretboard props
   (->> S
     (map-indexed
       (fn [i s]
         [:div.gtr-fretboard-row
          (row-fn {:key s}
            i s)
          (for [j (range N)]
            (let [h (+ s j)]
              (<fretboard-cell> {:key (str "row-" j)} show-string-heights
                N s i j
                cell-content)))
          ;; string
          [:div.gtr-string
           (string-fn {:style {:left (str (* 100 (cell-left-offset N 1)) "%")}}
             i s)]
          ])))
   ;; round marks on the fretboard
   (for [j (range N)
         :when (#{3 5 7 9} (mod j 12))]
     (<fretboard-round-mark> {:key (str "round-" j)}
       N j 3 round-diameter-px))
   (for [j (range N)
         v-offset [1.5 4.5]
         :when (and (= (mod j 12) 0) (> j 0))]
     (<fretboard-round-mark> {:key (str "round-12" j "-" v-offset)}
       N j v-offset round-diameter-px))
   ])

(defcard <fretboard-example>
  (<fretboard> {}
    {}
    (fn [N s i j h]
      [:div.gtr-fretboard-cell-content.gtr-cell-blue-on-hover
       {:style {:text-align "center"}
        :on-click (fn [_]
                    (synth/play [{:sound "acoustic" :duration 3
                                  :height h}]))}
       ])
    ))

;; ------------------------------------------------------------------------
;; Exercise: find where to play the note.

(defn init-find-cell []
  (let [s (rand-nth (standard-guitar-strings))
        j (rand-int 12)
        h (+ s j)]
    {:s s :h h
     :answered nil}))

(defc <find-cell-cell>
  < rum/static
  [h s1 i1 j1 h1 submitted? correct? submit!]
  #_(.log js/console "Rendering cell" i1 j1)
  [:div.gtr-fretboard-cell-content.gtr-cell-blue-on-hover
   {:key "content"
    :class (cond
             (and submitted? correct?) "find-cell-cell--right"
             submitted? "find-cell-cell--wrong"
             :else "")
    :on-click (fn [_]
                (synth/play (for [h [h1 h]]
                              {:sound "acoustic" :duration 1 :height h}))
                (submit! [s1 h1]))}
   (<fretboard-column-help> i1 j1)])

(defc <find-cell-view> < rum/static
  [s h answered
   correct? submit! next!]
  [:div
   [:p.text-center [:i "Play " [:strong (utk/<height> h)] " on " [:strong (utk/<height> s)] ":"]]
    (<fretboard> {:class "find-cell-fretboard"}
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
         (<find-cell-cell> h s1 i1 j1 h1 submitted? correct? submit!))))
   (cond
     correct? [:p "Well played!"
               (utk/<next-btn> {} next!)
               #_[:button.btn.btn-default.pull-right {:on-click next!}
                "Next"]]
     (some? answered) (let [[s1 h1] answered]
                        [:p (cond
                              (not= s s1) "Dude! that's not even the right string!"
                              :else "Nope, try again.")]
                        ))
   ])

(defn fce-submit!
  [a
   [s1 h1]]
  (swap! a assoc :answered [s1 h1]))

(defn fce-next!
  [a]
  (reset! a (init-find-cell)))

(defc find-cell-exo
  < rum/reactive
  [a]
  (let [{:as st :keys [s h answered]} (rum/react a)
        ;submit! (fn [[s1 h1]]
        ;          (swap! a assoc :answered [s1 h1]))
        ;next! (fn [] (reset! a (init-find-cell)))
        correct? (when answered
                   (let [[s1 h1] answered]
                     (and (= s1 s) (= h1 h))))]
    (<find-cell-view> s h answered correct?
      ;submit! next!
      (u/pfn fce-submit! a) (u/pfn fce-next! a))))

(defcard find-cell-exo-ex
  (find-cell-exo (u/rlatom ::fc1 init-find-cell)))

;; ------------------------------------------------------------------------------
;; Guitar map
;; IDEA: when hovering on the scale notes, lights up the cells

(defc <guitar-map-cell> < rum/static
  [i j h selected?]
  [:div.gtr-fretboard-cell-content.gtr-cell-blue-on-hover.guitar-map-cell
   {:class (if selected? "guitar-map-cell--selected" "")}
   [:div.gtr-cell-fret-text.guitar-map-fret-text
    {:key "a"}
    (when-not (= j 0)
      (repr/stringify-height h))]
   (<fretboard-column-help> i j)])

(defn gmap-toggle!
  [state
   note]
  (swap! state update :notes u/toggle-conj note))

(defc <guitar-map>
  < rum/reactive
  [props state fretboard-opts]
  (let [{:keys [notes]} (rum/react state)
        toggle! (u/pfn gmap-toggle! state)]
    [:div
     (<fretboard> {} (assoc fretboard-opts :show-string-heights true)
       (fn [N s i j h]
         (let [selected? (get notes (math/note-of-height h))]
           (<guitar-map-cell> i j h selected?))))

     [:div {:style {:textAlign "center"
                    :margin "20px"}}
      [:span.btn-group
       (->> math/all-notes
         (map-indexed
           (fn [i n1]
             [:button.btn.btn-default
              {:key (str "note-btn-" n1)
               :class (if (get notes n1) "active" "")
               :on-click #(toggle! n1)}
              (repr/stringify-note n1)])))]]]))

(defcard <guitar-map>
  (<guitar-map> {}
    (u/rlatom ::gm1 #(do {:notes #{(repr/parse-note "4")}}))
    {}))


