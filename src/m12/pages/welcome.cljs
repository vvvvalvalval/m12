(ns m12.pages.welcome
  (:require [clojure.string :as str]
            [rum.core :as rum]
            [sablono.core :as sab :include-macros true]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.lib.representations :as repr]
            [m12.widgets.ui-toolkit :as utk]
            [m12.utils :as u]
            [m12.widgets.piano-widgets]
            [m12.widgets.posts :as wp])
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
         [:td "Letter notation"]
         (for [n displayed-notes]
           [:td {:key (str n)}
            (repr/stringify-letter-note n)])]
        [:tr
         [:td "Solfège notation"]
         (for [n displayed-notes]
           [:td {:key (str n)}
            (repr/stringify-solfege-note n)])]
        [:tr
         [:td [:strong "M12 notation"]]
         (for [n displayed-notes]
           [:td {:key (str n)}
            [:strong (repr/stringify-note n)]])]]])
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
            [:strong [:i (repr/stringify-note n)]]])
         [:td {:key "octave"}
          [:strong [:i "10"]]]]]])
    ))

(defc <octaves-compare-table>
  [hs]
  [:table.table.table-bordered.text-center {:style {:maxWidth "500px" :margin "auto"}}
   [:tbody
    [:tr
     (for [h hs]
       [:td {:key h} (repr/stringify-letter-height h)])]
    [:tr
     (for [h hs]
       [:td {:key h} [:strong (utk/<height> h)]])]]])

(defc <welcome>
  []
  [:div.container
   [:h1.text-center "M12"]
   [:div {:style {:margin    "40px auto"
                  :max-width "600px"}}
    (m12.widgets.piano-widgets/<piano-scale-comparison>
      {} (u/rlatom ::psc m12.widgets.piano-widgets/psc-init))]
   [:p "I am experimenting with a alternative notation for music.
   This is simply a variant of "
    [:a {:href "https://en.wikipedia.org/wiki/Musical_notation#Integer_notation"
         :target "_blank"}
     "integer notation"]
    " which uses 12 digits instead of 10, which is why I'm calling it " [:strong "M12"]
    ". This website is an environment to test it and get familiar with it."]
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
                                  (map repr/parse-height)))]

   [:p "Likewise, all the Es end with a " [:strong "4"] ":"]
   [:div
    (<octaves-compare-table> (->> (str/split "24 34 44 54 64" #"\s+")
                               (map repr/parse-height)))]

   [:div
    ;; TODO
    [:h2 "Why a new notation?"]
    [:p "I'm currently a bit dissatisfied with the notations we currently use,
       espcecially the one - tabs - that guitarists use. I think we can do better."]

    [:p "In my view, a good notation for anything has the following qualities:"
     [:ol
      [:li "It's concise,"]
      [:li "It's practical to read and write,"]
      [:li "It makes the important " [:strong "obvious"] " and hides the irrelevant,"]
      [:li "By embodying appropriate concepts, it gives us a powerful "
       [:strong "mental model"] " to what it represents.
       In other words, it " [:em "helps us think"] " about what it stands for."]]

     ;; The history of other fields show that the notation has a crucial impact on the reach of our thought.
     ;; (examples: Arab vs Roman numbers, math equations, programming languages)
     ;;
     [:p "Therefore we need to ask: what " [:em "is"] " important
     when interpreting musical notation?
     In my view, it boils down to a few elementary questions:"
      [:ol
       [:li "Given a note, where does it fit on the scale ?"]
       [:li "Given a set of notes (a chord, a scale, a musical phrase...),
       what are the intervals by which these notes are related ?"]
       [:li "Given a base note, what is the note that is at some interval of this note ?"]]]

     [:p "Some notations are especially bad at this.
     For example, a guitar tab doesn't help you answer question 1 at all.
     Music sheet makes it easier, but not as easy as possible, because it's irregular.
     "]

     [:p "Once you represent notes and intervals as " [:em "numbers"] " (like computers do),
     you realize that answering those questions translates to very basic mental caculations,
     like addition and subtraction."]

     [:p "Here are some examples:"]
     [:div.row
      [:div.col-xs-12.col-md-6
       [:table.table
        [:thead
         [:tr
          [:th "English formulation"]
          [:th "M12 formulation"]]]
        [:tbody
         [:tr
          [:td
           (wp/<clrnote> "La5" 1) "is the" (wp/<clrivl> "5th" 2)
           "of" (wp/<clrnote> "Re5" 3)]
          [:td
           (wp/<equation> [:hs "59" 1] :- [:hs "52" 3] [:is "7" 2])
           ]]
         [:tr
          [:td
           "The" (wp/<clrivl> "minor 3rd" 5) "of" (wp/<clrnote> "Si3" 4)
           "is" (wp/<clrnote> "Re3" 6)]
          [:td
           (wp/<equation> [:hs "3b" 4] :+ [:is "3" 5] [:hs "42" 6])]]
         [:tr
          [:td "{" (wp/<clrnote> "Re3" 1) "," (wp/<clrnote> "Fa#3" 2) "," (wp/<clrnote> "La3" 3) "}"
           " form a" (wp/<clrivl> "major chord" 5)]
          [:td
           [:div (wp/<equation> [:hs "32" 1] :+ [:is "0" 5] [:hs "32" 1])]
           [:div (wp/<equation> [:hs "32" 1] :+ [:is "4" 5] [:hs "36" 2])]
           [:div (wp/<equation> [:hs "32" 1] :+ [:is "7" 5] [:hs "39" 3])]
           ]]
         [:tr
          [:td "The" (wp/<clrivl> "5th" 1) "of the" (wp/<clrivl> "5th" 2)
           "is the" (wp/<clrivl> "major 2nd" 3)]
          [:td
           (wp/<equation> [:sns "7" 2] :+ [:sns "7" 1] [:sns "2" 3])]]]]]]

     ;; This is where the notation can help you: by using a few mental caculation tricks, you can build a mental map of music
     ;; much faster than if you just learned by heart the intervals between every pair of notes. Cf the "counting with notes" section below.
     ]

    ]

   [:div
    [:h2 "Counting with notes"]
    ;; Example of notes and intervals additions and subtractions.
    ;; Can click on a button to play (interval as superposition of notes).
    ;; it doesn't make sense to add 2 notes. It does make sense to add two intervals.

    [:h3 "Scale notes"
     ;; explanation: in many respects, notes that are exactly one our several octaves (12 half-tones) apart
     ;; can be considered the same, e.g a Sol4 is considered the same as a Sol5, and we just call it a 'Sol'.
     ;; In M12 notation, instead of writing it 47 or 57, we just write it 7.
     ;; Likewise, an interval of 3 half-tones is considered the same as an interval of 15 half-tones or an interval of
     ;; -9 half-tones, and we call it a 'minor 3rd'. In M12 notation, we write it 3.
     ;;
     ;; From this simplified view, the set of all notes (and intervals) forms a cycle.
     ;; We call the 12 notes in this cycle the *scale notes*.
     ;;
     ;; TODO cycle representation of scale notes.
     ;;
     ;; Once you have learned how to add and subtract scale notes, it's very easy to add and subtract notes and intervals.
     ;; So we'll start by practicing that.

     ;; TODO examples of scale notes addition and subtraction.
     ;; hover / (?) button to show this on a cycle representation.

     ;; If you have a good memory, you can simply write the addition and subtraction tables
     ;; for all 12 scale notes and learn them by heart. They are given below:
     ;; TODO: scale notes addition and subtraction tables
     ;;
     ;; TODO addition widget

     ;;
     ;; TODO find the complement
     ;;

     ;; TODO strong and weak points
     ]

    ]

   [:div
    [:h2 "For guitarists"]]

   ])
