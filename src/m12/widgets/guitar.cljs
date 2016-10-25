(ns m12.widgets.guitar
  (:require [rum.core :as rum]
            [sablono.core :as sab :include-macros true]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.widgets.ui-toolkit :as utk]
            [clojure.string :as str])
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

(defc <fretboard-cell>
  [props
   cell-height-px
   N s i j
   cell-content]
  (let [h (+ s j)]
    [:div.gtr-fretboard-cell
     (assoc props
       :class (if (= j 0) "gtr-fretboard-cell--leftmost" "")
       :style {:height (str cell-height-px "px")
               :width (str (* 100 (cell-width N j)) "%")}
       )
     (cell-content N s i j h)
     (when (= j 0)
       [:div {:style {:font-size "12px"
                      :position "absolute"
                      :right "0"
                      :top (str (* 100 6e-3 cell-height-px) "%")
                      :text-align "right"}
              }
        (math/stringify-height h)])
     ]))

(defc <fretboard-round-mark> < rum/static
  [props N j v-offset height-px diameter-px]
  [:div.gtr-round-wrapper
   (assoc props
     :style {:left (str (* 100 (+ (cell-left-offset N j)
                                 (/ (cell-width N j) 2))) "%")
             :top (str (* v-offset height-px) "px")
             :width (str diameter-px "px")
             :height (str diameter-px "px")
             })
   [:div.gtr-round
    {:style {:height (str diameter-px "px")
             :width (str diameter-px "px")}}]])

(defn standard-guitar-strings
  []
  (->> (str/split "24 29 32 37 3b 44" #" ")
    reverse (mapv math/parse-height)))

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
    :keys [N S cell-height-px round-diameter-px
           row-fn string-fn]
    :or {N 13
         S (standard-guitar-strings)
         cell-height-px 24
         round-diameter-px 10
         row-fn identity
         string-fn identity}}
   cell-content]
  [:div.gtr-fretboard props
   (->> S
     (map-indexed
       (fn [i s]
         [:div.gtr-fretboard-row
          (row-fn {:key s :style {:height (str cell-height-px "px")}}
            i s)
          (for [j (range N)]
            (let [h (+ s j)]
              (<fretboard-cell> {:key (str "row-" j)} cell-height-px
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
       N j 3 cell-height-px round-diameter-px))
   (for [j (range N)
         v-offset [1.5 4.5]
         :when (and (= (mod j 12) 0) (> j 0))]
     (<fretboard-round-mark> {:key (str "round-12" j "-" v-offset)}
       N j v-offset cell-height-px round-diameter-px))
   ])

(defcard <fretboard>
  (<fretboard> {}
    {:cell-height-px 25}
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
  [h s1 h1 submitted? correct? submit!]
  [:div.gtr-fretboard-cell-content.gtr-cell-blue-on-hover
   {:style (cond-> {:cursor "pointer"}
             submitted? (assoc :backgroundColor (if correct? "green" "red")))
    :on-click (fn [_]
                (synth/play (for [h [h1 h]]
                              {:sound "acoustic" :duration 1 :height h}))
                (submit! [s1 h1]))}
   ])

(defc <find-cell-view> < rum/static
  [s h answered
   correct? submit! next!]
  [:div
   [:p "Find the cell of string " [:strong (math/stringify-height s)]
    " where you can play " [:strong (math/stringify-height h)]]
   (<fretboard> {}
     {:string-fn (fn [props i1 s1]
                   (cond-> props
                     (= s s1)
                     (update :style assoc
                       :borderColor "blue")))}
     (fn [_ s1 i1 j1 h1]
       (let [submitted? (when answered
                          (let [[s2 h2] answered]
                            (and (= s2 s1) (= h2 h1))))]
         (<find-cell-cell> h s1 h1 submitted? correct? submit!))))
   (cond
     correct? [:p "Well played!"
               [:button.btn.btn-default.pull-right {:on-click next!}
                "Next"]]
     (some? answered) (let [[s1 h1] answered]
                        [:p (cond
                              (not= s s1) "Dude! that's not even the right string!"
                              :else "Nope, try again.")]
                        ))
   ])

(defcs find-cell-exo
  < (rum/local (init-find-cell) ::state)
  [state]
  (let [a (::state state)
        {:as st :keys [s h answered]} @a
        submit! (fn [[s1 h1]]
                  (swap! a assoc :answered [s1 h1]))
        next! (fn [] (reset! a (init-find-cell)))
        correct? (when answered
                   (let [[s1 h1] answered]
                     (and (= s1 s) (= h1 h))))]
    (<find-cell-view> s h answered correct? submit! next!)))

(defcard find-cell-exo
  (find-cell-exo))

