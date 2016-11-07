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
            [m12.widgets.posts :as wp]
            [m12.widgets.scale-cycle :as scyc]
            [m12.widgets.scale-cycle.notes-sets]
            [m12.widgets.arithmetic]
            [m12.widgets.guitar]
            [m12.lib.games.components :as gamec]
            [m12.widgets.games.exS :as exS]
            [m12.widgets.games.exG1 :as exG1]
            [m12.widgets.games.exG2 :as exG2]
            )
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
    [:div.text-center
     [:h4 "Notes"]
     [:table.table.table-bordered
      [:thead
       [:tr
        [:th.text-center "Letter"]
        [:th.text-center "Solfège"]
        [:th.text-center "M12"]]]
      [:tbody
       (for [n displayed-notes]
         [:tr {:key (str n)}
          [:td (repr/stringify-letter-note n)]
          [:td (repr/stringify-solfege-note n)]
          [:td [:strong (repr/stringify-note n)]]])
       ]]]
    ))

(defc <scale-intervals-notations-comparison-table>
  < rum/static
  []
  (let [displayed-notes math/all-notes]
    [:div.text-center
     [:h4 "Intervals"]
     [:table.table.table-bordered
      [:thead
       [:tr
        [:th.text-center "Classical"]
        [:th.text-center "M12"]]]
      [:tbody
       (for [[m12 english]
             (map vector
               (concat
                 (->> math/all-notes (map repr/stringify-note))
                 ["10"])
               (str/split "unisson,minor 2nd, major 2nd, minor 3rd, major 3rd, 4th, diminished 5th,
        5th, minor 6th, major 6th, minor 7th, major 7th, octave"
                 #"\s*,\s*"))]
         [:tr {:key m12}
          [:td english]
          [:td [:i [:strong m12]]]])
       ]]]
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

(def ^:private scale-cycle-into-f-note-props
  (let [base-height (repr/parse-height "40")]
    (fn [props note _]
      (assoc props
        :class "scale-cycle-hoverable"
        :on-click #(synth/play
                    [{:sound "piano" :duration 2
                      :height (+ base-height note)}])))))


(defc <scale-cycle-intro>
  < rum/static rum/reactive
  [state]
  (let [{:keys [notation]} (rum/react state)]
    [:div.text-center {:style {:padding "20px 0"}}
     [:div {:style {:margin "10px 0"}}
      (utk/<notation-selector> {} notation #(swap! state assoc :notation %))]
     (scyc/scale-cycle {}
       {:width 200
        :f-note-props scale-cycle-into-f-note-props
        :f-note-text (repr/stringifier-for-notation notation)})
     [:div
      [:i "The 12 scale notes"]
      [:br]
      [:i "(click to play)"]]
     ]))

(defc <welcome>
  []
  [:div.container
   [:h1.text-center "M12"]
   [:div {:style {:margin "40px auto"
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
   (figure "Notations for notes and intervals"
     [:div.row
      [:div.col-sm-6.col-md-4.col-md-offset-2.col-lg-3.col-lg-offset-3
       (<scale-notations-comparison-table>)]
      [:div.col-sm-6.col-md-4.col-lg-3
       (<scale-intervals-notations-comparison-table>)]])

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
     (figure "M12 vs English"
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
             (wp/<equation> [:sns "7" 2] :+ [:sns "7" 1] [:sns "2" 3])]]]]]])

     [:p
      "This is where the notation can help you:
      by using a few mental caculation tricks, you can build a mental map of music
      much faster than if you just learned by heart the intervals between every pair of notes.
      Cf the \"counting with notes\" section below."]
     ]

    ]

   [:div
    [:h2 "Counting with notes"]
    ;; Example of notes and intervals additions and subtractions.
    ;; Can click on a button to play (interval as superposition of notes).
    ;; it doesn't make sense to add 2 notes. It does make sense to add two intervals.

    ;; TODO general height addition and subtraction widget ?

    [:h3 "Scale notes"

     ]
    [:p "In many respects, notes that are exactly one our several octaves (12 semitones) apart
     can be considered the same, e.g a Sol4 is considered the same as a Sol5, and we just call it a 'Sol'.
     In M12 notation, instead of writing it " [:strong (utk/<height> (repr/parse-height "47"))] " or "
     [:strong (utk/<height> (repr/parse-height "57"))] ",
      we just write it " [:strong (utk/<note> (repr/parse-note "7"))] "."]

    [:p "Likewise, an interval of 3 semitones is considered the same as
    an interval of 15 semitones or an interval of -9 (i.e going down the scale) semitones,
     and we call it a 'minor 3rd'. In M12 notation, we write it "
     [:strong (utk/<note> (repr/parse-note "3"))] "."]

    [:p "From this simplified view, the set of all notes (and intervals) forms a cycle:"]

    (<scale-cycle-intro> (u/rlatom ::scintro (constantly {:notation :m12})))

    [:p "We call the 12 notes in this cycle the " [:strong [:em "scale notes"]] "."]

    [:p "This cycle representation is useful for displaying sets of notes
    (such as chords and scales) as visual patterns, as shown in the figure below:"]

    (figure "Exploring the cycle representation"
      (m12.widgets.scale-cycle.notes-sets/<scale-notes-sets-browser>
        (u/rlatom ::scsb m12.widgets.scale-cycle.notes-sets/scale-notes-sets-browser-init)))

    [:p "For instance, you can see that all major chords form the same pattern with various rotations.
    Minor chords form a different pattern. In musical terms, a 'transposition' translates to a rotation."]

    [:p "Once you have learned how to add and subtract scale notes,
    it becomes very easy to add and subtract notes and intervals.
    So we'll start by practicing that."]


    ;; TODO examples of scale notes addition and subtraction.
    ;; hover / (?) button to show this on a cycle representation.

    [:p "If you have a good memory, you can simply write the addition and subtraction tables - just like in elementary school -
    for all 12 scale notes and learn them by heart. They are given below:"]

    (figure "Addition and subtraction tables for scale notes"
      (m12.widgets.arithmetic/<op-tables>))

    [:p "If you already feel confident with this, you can train with the following exercise:"]

    (figure "Ex S1: General addition of scale notes"
      (gamec/<game-in-rlatom> exS/s1 ::s1 nil
        (fn [_ problem sa correct? submit! next!]
          (exS/<add-scale-notes-view> {} problem sa correct? submit! next!))))

    [:h4 "Counting tricks: the complement"]

    [:p "Personally, I have a very bad memory, so it would be very difficult for me to learn all 248 rows of these tables by heart.
    Fortunately, there are several mental calculation tricks you can use to avoid remembering everything."]

    [:p "The first one consists of learning the " [:em "complement"] " of each note in the scale.
    For instance, " [:strong (utk/<note> 3)] " is the complement of " [:strong (utk/<note> 9)] ",
    because going 3 steps up the scale is like going 9 steps down the scale.
    Similarly, " [:strong (utk/<note> 7)] " is the complement of " [:strong (utk/<note> 5)] ", "
     [:strong (utk/<note> 2)] " is the complement of " [:strong (utk/<note> 10)] ", "
     "and " [:strong (utk/<note> 6)] " is the complement of itself."]

    [:div.text-center
     [:div [:span.equation (wp/<clrnote> "-3" 0)] "=" (wp/<clrnote> "9" 0)]
     [:div [:span.equation (wp/<clrnote> "-7" 0)] "=" (wp/<clrnote> "5" 0)]
     [:div [:span.equation (wp/<clrnote> "-2" 0)] "=" (wp/<clrnote> "a" 0)]
     [:div [:span.equation (wp/<clrnote> "-6" 0)] "=" (wp/<clrnote> "6" 0)]]

    ;; TODO explanation of why this helps.

    [:p "You can practice with complements here:"]

    (figure "Ex S2: Find the complement"
      (gamec/<game-in-rlatom> exS/s2 ::s2 nil
        (fn [_ problem sa correct? submit! next!]
          (exS/<add-scale-notes-view> {} problem sa correct? submit! next!))))

    (figure "Ex S3a: the cycle of 4s"
      (gamec/<game-in-rlatom> exS/s3a ::s3a nil
        (fn [_ problem sa correct? submit! next!]
          (exS/<s3a> {} problem sa correct? submit! next!))))

    (figure "Ex S3b: the cycle of 3s"
      (gamec/<game-in-rlatom> exS/s3b ::s3b nil
        (fn [_ problem sa correct? submit! next!]
          (exS/<s3b> {} problem sa correct? submit! next!))))


    ;; TODO hard additions (S1a)

    ]

   [:div
    [:h2 "For guitarists"]
    ;; TODO option for changing notation
    (figure "M12 guitar map"
      (m12.widgets.guitar/<guitar-map> {}
        (u/rlatom ::gm1 (constantly {:notes #{4}}))
        {}))

    ;; TODO config UI for set of strings and change notation
    (figure "Ex G1: find where to play the note on the tab."
      (gamec/<game-in-rlatom> exG1/g1 ::G1 nil
        (fn [_ problem sa correct? submit! next!]
          (exG1/<G1> problem sa correct? submit! next!))))

    (figure "Ex G2: find the note played on the tab"
      (gamec/<game-in-rlatom> exG2/g2 ::G2 nil
        (fn [_ problem sa correct? submit! next!]
          (exG2/<G2> problem sa correct? submit! next!))))

    ;; TODO play based on tab
    ;; TODO adding exercise based on

    ]

   ])
