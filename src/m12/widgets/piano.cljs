(ns m12.widgets.piano
  (:require [m12.lib.math :as math]
            [sablono.core :as sab]
            [m12.widgets.ui-toolkit :as utk]
            [rum.core :as rum]
            [m12.lib.representations :as repr])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defn white-height? [h]
  (#{0 2 4 5 7 9 11} (math/note-of-height h)))

(defn black-height? [h]
  (not (white-height? h)))

(defn key-offset [h]
  (+
    (* 7 (quot h 12))
    (case (mod h 12)
      0 0
      1 0.5
      2 1
      3 1.5
      4 2
      5 3
      6 3.5
      7 4
      8 4.5
      9 5
      10 5.5
      11 6
      )))

(defc <piano-keyboard>
  < rum/static
  [props
   {:as opts
    :keys [min-h max-h
           key-props-fn
           key-content]
    :or {key-props-fn (fn [props h] props)
         key-content (fn [h] [:div])}}]
  (let [min-h (if (white-height? min-h) min-h (dec min-h))
        max-h (if (white-height? max-h) max-h (inc max-h))
        total-width (inc (- (key-offset max-h) (key-offset min-h)))
        key-width (/ 1 total-width)]
    [:div.piano-keyboard props
     (for [h (range min-h (inc max-h))]
       (let [props {:key (str "piano-key-" h)
                    :style {:left (str (* (- (key-offset h) (key-offset min-h))
                                         key-width 100) "%")
                            :width (str (* 100 key-width) "%")}}]
         (if (white-height? h)
           [:div.piano-key (-> props
                             (assoc :class "piano-key--white")
                             (key-props-fn h))
            (key-content h)]
           [:div.piano-key (-> props
                             (assoc :class "piano-key-black-container"))
            [:div.piano-key--black (-> {} (key-props-fn h))
             (key-content h)]
            ]))
       )]))

(defcard <piano-keyboard>-ex
  (sab/html
    (into [:div]
      (->> [["29" "54"]
            ["29" "55"]
            ["2a" "56"]]
        (map-indexed
          (fn [i [minhs maxhs]]
            [:div
             [:h5 "From " minhs " to " maxhs]
             (<piano-keyboard> {:key (str i)}
               {:min-h (repr/parse-height minhs)
                :max-h (repr/parse-height maxhs)
                :key-content (fn [h]
                               [:div.bottom-stretched
                                {:style {:text-align "center"
                                         :color
                                         (if (white-height? h)
                                           "black" "white")}}
                                (utk/<height> (repr/stringify-height h))])}
               )]))
        (interpose [:br]))
      )
    ))
