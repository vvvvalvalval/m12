(ns m12.pages.welcome
  (:require [clojure.string :as str]
            [rum.core :as rum]
            [sablono.core :as sab :include-macros true]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.widgets.ui-toolkit :as utk]
            [m12.utils :as u]
            [m12.widgets.piano-widgets])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defn figure [title body]
  [:div.panel.panel-default.m12-max-width
   [:div.panel-heading [:h5 title]]
   [:div.panel-body body]])

(defc <scale-notations-comparison-table>
  < rum/static
  []
  (let [displayed-notes math/all-notes]
    (figure "Representation of notes in the scale:"
      [:table.table.table-bordered.text-center
       [:tbody
        [:tr
         [:td "Classical notation"]
         (for [n displayed-notes]
           [:td {:key (str n)}
            (math/stringify-classic-note n)])]
        [:tr
         [:td "Solfege notation"]
         (for [n displayed-notes]
           [:td {:key (str n)}
            (math/stringify-solfege-note n)])]
        [:tr
         [:td [:strong "M12 notation"]]
         (for [n displayed-notes]
           [:td {:key (str n)}
            [:strong (math/stringify-note n)]])]]])
    ))

(defc <scale-intervals-notations-comparison-table>
  < rum/static
  []
  (let [displayed-notes math/all-notes]
    (figure "Representation of intervals"
      [:table.table.table-bordered.text-center
       [:tbody
        [:tr
         [:td "Classical notation"]
         (for [iv (str/split "unisson,minor 2nd, major 2nd, minor 3rd, major 3rd, 4th, diminished 5th,
        5th, minor 6th, major 6th, minor 7th, major 7th, octave"
                    #"\s*,\s*" )]
           [:td {:key (str iv)} iv])]
        [:tr
         [:td [:strong "M12 notation"]]
         (for [n displayed-notes]
           [:td {:key (str n)}
            [:strong [:i (math/stringify-note n)]]])
         [:td {:key "octave"}
          [:strong [:i "10"]]]]]])
    ))

(defc <octaves-compare-table>
  [hs]
  [:table.table.table-bordered.text-center {:style {:maxWidth "500px" :margin "auto"}}
   [:tbody
    [:tr
     (for [h hs]
       [:td {:key h} (math/stringify-classic-height h)])]
    [:tr
     (for [h hs]
       [:td {:key h} [:strong (utk/<height> h)]])]]])

(defc <welcome>
  []
  [:div.container
   [:h1.text-center "M12"]
   [:div {:style {:margin "40px auto"
                  :max-width "600px"}}
    (m12.widgets.piano-widgets/<piano-scale-comparison>
      {} (u/rlatom ::psc m12.widgets.piano-widgets/psc-init))]
   [:p "I am experimenting with a new notation for music.
   This website is an environment to test it and get familiar with it."]
   [:p "The idea is to simply represent notes and intervals as numbers, like so:"]
   (<scale-notations-comparison-table>)
   [:p "Similarly for intervals:"]
   (<scale-intervals-notations-comparison-table>)

   [:h3 "12 digits"]
   [:p "There are 12 notes in the scale, not 10.
   So, instead of writing numbers with 10 digits, as we usually do:"
    [:blockquote "1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15..."]
    "...we will write them with " [:em 12] " digits:"
    [:blockquote "1, 2, 3, 4, 5, 6, 7, 8, 9, a, b, 10, 11, 12, 13..."]]
   "We do this by adding 2 digits, " [:strong "a"] " and " [:strong "b"]
   ", to the 10 digits we're already familiar with."

   [:p "The advantage is that, by looking at a note written as a 12-digit number,
   we immediately where it is in the scale."]

   [:p "For instance, all the Cs end with a " [:strong "0"] ":"]
   [:div
    (<octaves-compare-table> (->> (str/split "20 30 40 50 60" #"\s+")
                               (map math/parse-height)))]

   [:p "Likewise, all the Es end with a " [:strong "4"] ":"]
   [:div
    (<octaves-compare-table> (->> (str/split "24 34 44 54 64" #"\s+")
                               (map math/parse-height)))]
   ;; TODO representation of a couple octaves on the piano, with an option to switch the notation.

  #_[:div
   [:h2 "Why a new notation?"]

   ]

  [:div
   [:h2 "The math of music"]]

   ])
