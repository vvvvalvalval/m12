(ns m12.widgets.scale-cycle.notes-sets
  (:require [rum.core :as rum]
            [m12.lib.math :as math]
            [m12.widgets.ui-toolkit :as utk]
            [m12.lib.representations :as repr]
            [m12.widgets.scale-cycle :as scyc]
            )
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]])
  )


(defn scale-notes-sets-browser-init []
  {:notation :m12
   :fundamental 0
   :set-type [:chord :major]})

(defn snsb-notes-set
  [fundatemental set-type]
  (let [[kind data] set-type]
    (case kind
      :chord (case data
               :major (math/major-chord-notes fundatemental)
               :minor (math/minor-chord-notes fundatemental))
      :scale (case data
               :major (math/major-scale-notes fundatemental)
               :harmonic-major (math/harmonic-scale-notes fundatemental)
               :double-harmonic-major (math/double-harmonic-scale-notes fundatemental))
      :mode (math/mode-notes fundatemental data))))

(def snsb-set-types
  [["Major chord" [:chord :major]]
   ["Minor chord" [:chord :minor]]
   ["Major scale" [:scale :major]]
   ["Harmonic major scale" [:scale :harmonic-major]]
   ["Double harmonic major scale" [:scale :double-harmonic-major]]
   ["Mode: Ionian" [:mode :ionian]]
   ["Mode: Dorian" [:mode :dorian]]
   ["Mode: Phrygian" [:mode :phrygian]]
   ["Mode: Lydian" [:mode :lydian]]
   ["Mode: Mixolydian" [:mode :mixolydian]]
   ["Mode: Aeolian" [:mode :aeolian]]
   ["Mode: Locrian" [:mode :locrian]]
   ])

(defc <scale-notes-sets-browser>
  < rum/static rum/reactive
  [state]
  (let [{:keys [notation fundamental set-type]} (rum/react state)]
    [:div.text-center {:style {:padding "20px 0"}}
     [:div {:style {:margin "10px 0"}}
      (utk/<notation-selector> {} notation #(swap! state assoc :notation %))]
     (scyc/cycle-notes-set {}
       {:width 200
        :f-note-text (case notation
                       :m12 repr/stringify-note
                       :solfege repr/stringify-solfege-note
                       :letter repr/stringify-letter-note)}
       (snsb-notes-set fundamental set-type))
     [:div.form-inline
      [:div.form-group.m12-inline-block
       [:label.m12-text-spaced "Tonic note:"]
       (utk/select {:class "form-control m12-inline-block m12-text-spaced"
                    :style {:width "auto" :display "inline-block"}}
         {::utk/to-value repr/stringify-note
          ::utk/from-value repr/parse-note
          ::utk/option-text (repr/stringifier-for-notation notation)}
         fundamental
         #(swap! state assoc :fundamental %)
         math/all-notes)]
      [:div.form-group.m12-inline-block
       [:label.m12-text-spaced "Pattern:"]
       (let [opts (vec (map-indexed (fn [i st] [i st]) snsb-set-types))
             selected (->> opts (filter (fn [[i [text tuple]]] (= tuple set-type))) first)]
         (utk/select {:class "form-control m12-inline-block m12-text-spaced"
                      :style {:width "auto" :display "inline-block"}}
           {::utk/to-value (fn [[i st]] (str i))
            ::utk/from-value (fn [s] (nth opts (js/parseInt s) ))
            ::utk/option-key first
            ::utk/option-text (fn [[i [text data]]] text)}
           selected
           (fn [[i [_ tuple]]] (swap! state assoc :set-type tuple))
           opts))]
      ]
     ]))

