(ns m12.widgets.notation
  (:require [rum.core :as rum]
            [sablono.core :as sab :include-macros true]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.widgets.ui-toolkit :as utk]
            [m12.lib.representations :as repr])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defn notations-head
  ([] (notations-head 0))
  ([additional-columns]
   [:thead
    (into
      [:tr
       [:th "Dozenal notation"]
       [:th "Solfege notation"]
       [:th "English notation"]
       ]
      (repeat additional-columns [:th ""]))]))

;; ------------------------------------------------------------------------

(declare
  <translation-table>
    <translation-table-row-)

(defc <translation-table>
  []
  [:table.table
   (notations-head)
   [:tbody
    (for [n math/all-notes]
      (rum/with-key
        (<translation-table-row- n)
        n))]
   ])

(defc <translation-table-row- [n]
  [:tr
   [:td (repr/stringify-note n)]
   [:td (repr/stringify-solfege-note n)]
   [:td (repr/stringify-letter-note n)]
   ])

(defcard translations-table
  "Quick reference for converting notes to/from the various notations."
  (<translation-table>))

;; ------------------------------------------------------------------------

(declare
  <translator>
    <translator-view>)

(defcs <translator> < (rum/local (repr/parse-height "39") ::h)
  [state]
  (<translator-view> @(::h state) #(reset! (::h state) %)))

(defc <translator-view> < rum/static
  [chosen-h set-h!]
  [:table.table
   (notations-head 1)
   [:tbody
    [:tr
     (for [[stringify key] [[repr/stringify-height "m12"]
                            [repr/stringify-solfege-height "solfege"]
                            [repr/stringify-letter-height "classic"]
                            ]]
       [:td {:key key}
        (utk/select {:class "form-control"}
          {::utk/from-value #(js/parseInt %)
           ::utk/to-value str
           ::utk/option-text stringify}
          chosen-h set-h! math/all-heights)])
     [:td (utk/<play-btn> {} [{:sound "piano" :height chosen-h :duration 3}])]
     ]]])

(defcard <translator>
  "Lets you translate interactively to/from Dozenal notation and usual notations."
  (<translator>))


