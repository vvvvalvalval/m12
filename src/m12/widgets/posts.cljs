(ns m12.widgets.posts
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]])
  (:require [rum.core :as rum]
            [m12.lib.math :as math]
            [sablono.core :as sab]
            [m12.lib.representations :as repr]
            [m12.widgets.ui-toolkit :as utk]))

(def ^:private dark-colors
  ["black"
   "blue"
   "red"
   "green"
   "purple"
   "tomato"
   "cornflowerblue"
   "brown"
   "deeppink"
   "chocolate"
   "dimgrey"
   "cadetblue"
   ])

(defn nth-color [n]
  (nth dark-colors (mod n (count dark-colors))))

(defc <clrnote> < rum/static
  "A coloured note"
  [text k]
  [:span.clrnote
   {:style {:color (nth-color k)}}
   text])

(defcard colored-notes
  (sab/html
    [:div
     (->> math/all-notes
       (map-indexed
         (fn [i n]
           (<clrnote> (utk/<height> (+ (repr/ph "30") n)) i))))]))

(defc <clrivl>
  < rum/static
    "A coloured interval"
  [text k]
  [:span.clrivl {:style {:color (nth-color k)}}
   text])

(defc <clrsn>
  < rum/static
  "A colored scale note"
  [text k]
  [:span.clrsn {:style {:color (nth-color k)}}
   text])

(defc <equation-member>
  < rum/static
  [[tag content k]]
  (let [k (or k 0)]
    (case tag
      :hs (<clrnote> (-> content repr/parse-height utk/<height>) k)
      :is (<clrivl> (-> content repr/parse-height utk/<height>) k)
      :sns (<clrsn> (-> content repr/parse-note) k)
      content)))

(defc <equation>
  < rum/static
  [x sign y z]
  [:span.equation
   (<equation-member> x)
   (case sign :+ "+" :- "-")
   (<equation-member> y)
   "="
   (<equation-member> z)])

