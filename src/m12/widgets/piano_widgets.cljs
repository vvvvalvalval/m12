(ns m12.widgets.piano-widgets
  (:require [m12.widgets.piano :as piano]
            [rum.core :as rum]
            [m12.lib.math :as math]
            [m12.widgets.ui-toolkit :as utk]
            [m12.utils :as u]
            [m12.services.synth :as synth])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defn psc-init []
  {:hovered-h #{}
   :notation :m12
   :played {}})

(defn psc-key-props-fn
  [state played
   props h]
  (-> props
    (update :class
      #(str %
        " piano-scale-comp-key"
        (when (-> h (played 0) (> 0))
          " piano-scale-comp-key--played")
        ))
    (assoc
      :on-click
      (fn [_]
        (let [dur 3]
          (synth/play [{:height h
                        :duration dur :sound "piano"}])
          (swap! state update-in [:played h] #(inc (or % 0)))
          (js/setTimeout (fn []
                           (swap! state update-in [:played h] dec))
            (* dur 1000))))
      :on-mouse-enter
      (fn [_] (swap! state update :hovered-h conj h))
      :on-mouse-leave
      (fn [_] (swap! state update :hovered-h disj h))
      )))

(defc <psc-key-content>
  < rum/static
  [notation h]
  [:div.bottom-stretched.piano-scale-comp-key-text
   {:style {:text-align "center"
            :color
            (if (piano/white-height? h)
              "black" "white")}}
   (case notation
     :m12
     (utk/<height> h)
     :solfege
     [:span.piano-key-bottom-note
      (math/stringify-solfege-height h)]
     :classical
     [:span.piano-key-bottom-note
      (math/stringify-classic-height h)]
     )])

(defc <piano-scale-comparison>
  < rum/static rum/reactive
  [props state]
  (let [{:keys [hovered-h notation played]} (rum/react state)]
    [:div
     [:div.text-center {:style {:min-height "60px"}}
      [:span.btn-group
       (for [[nt nt-name] [[:m12 "M12"]
                           [:solfege "solfege"]
                           [:classical "classical"]
                           ]]
         [:button.btn.btn-default
          (cond-> {:key nt-name
                   :style {:width "80px"}
                   :on-click (fn [_] (swap! state assoc :notation nt))}
            (= notation nt) (assoc :class "active"))
          nt-name])]
      [:div
       (for [nt [:m12 :solfege :classical]]
         [:strong
          {:key (str nt)
           :style {:width "80px"
                   :display "inline-block"}}
          (if-let [h (first hovered-h)]
            (case nt
              :m12
              (utk/<height> h)
              :solfege
              (math/stringify-solfege-height h)
              :classical
              (math/stringify-classic-height h))
            " ")
          ])]]
     (piano/<piano-keyboard> {}
       {:min-h (math/parse-height "40")
        :max-h (math/parse-height "5b")
        :key-props-fn
        (partial psc-key-props-fn state played)
        :key-content
        (fn [h] (<psc-key-content> notation h))
        })]))

(defcard <piano-scale-comparison>-ex
  (<piano-scale-comparison> {}
    (u/rlatom ::psc1 psc-init)))
