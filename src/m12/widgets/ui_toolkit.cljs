(ns m12.widgets.ui-toolkit
  (:require [rum.core :as rum]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [clojure.string :as str])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defc <play-btn> [{text ::text
                 :or {text "Play"}}
                  notes]
  [:button.btn.btn-default
   {:on-click (fn [_] (synth/play notes))}
   "Play"])

(defcard play-btn
  (<play-btn> {::text "Hit me!"}
    (for [h [42 45 49]]
      {:duration 3 :sound "organ" :height h})))

(defc select < rum/static
  "Generic select form."
  [props
   {:as opts,
    to-value ::to-value
    from-value ::from-value
    option-text ::option-text
    option-key ::option-key
    :or {option-key str
         to-value str
         from-value identity
         option-text str}}
   selected
   choose!
   options]
  [:select (assoc props
             :value (to-value selected)
             :on-change (fn [e]
                          (choose! (-> e .-target .-value from-value))
                          :done))
   (for [o options]
     [:option {:value (to-value o) :key (option-key o)}
      (option-text o)])])

(defcard select-example
  (let [a (atom 7)
        options (vec (range 24))]
    (select {:class "form-control"}
      {::to-value #(.toString % 2)
       ::from-value #(js/parseInt % 2)
       ::option-text #(str % " (in base 10)")
       ::option-key inc}
      @a #(reset! a %)
      options)
    ))

(defc <note> [n]
  [:span (math/stringify-note n)])


(defcs note-input
  < (let [cb (fn [state]
               (let [[props {:keys [on-mounted!]
                             :or {on-mounted! #(do nil)}}] (:rum/args state)]
                 (on-mounted! (rum/dom-node state))
                 state))]
      {:did-mount cb :did-update cb})
  [state props {:as opts :keys [on-new-value! on-clear! on-mounted!]
          :or {on-clear! #(do nil)}}]
  [:input (assoc props
            :type "text"
            :on-change (fn [e]
                         (let [new-val (-> e .-target .-value)]
                           (cond
                             (str/blank? new-val)
                             (on-clear!)

                             (not (math/valid-note? new-val))
                             (do
                               (on-clear!)) ;; TODO show validation error (Val, 23 Oct 2016)

                             :else
                             (let [answer (math/parse-note new-val)]
                               (on-new-value! answer))
                             ))
                         :done))
   ])
