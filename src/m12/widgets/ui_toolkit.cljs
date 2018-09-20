(ns m12.widgets.ui-toolkit
  (:require [rum.core :as rum]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.lib.representations :as repr]
            [clojure.string :as str]
            [m12.utils :as u]
            [m12.utils.dom :as dom])
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
  (let [a (u/rlatom ::select-ex (constantly 7))
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
  [:span (repr/stringify-note n)])


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

                             (not (repr/valid-note? new-val))
                             (do
                               (on-clear!)) ;; TODO show validation error (Val, 23 Oct 2016)

                             :else
                             (let [answer (repr/parse-note new-val)]
                               (on-new-value! answer))
                             ))
                         :done))
   ])

(defc <height>
  < rum/static
  [h]
  (let [s (str/reverse (repr/stringify-height h))]
    [:span.m12-h
     [:span.m12-h1 (nth s 1)]
     [:span.m12-h0 (nth s 0)]]
    ))

(defc <scale-note>
  < rum/static
  [n]
  [:span.m12-sn (repr/stringify-note n)])

(def focus-when-updated
  (let [cb (fn [state]
             (dom/focus (rum/dom-node state))
             state)]
    {:did-mount cb :did-update cb}))

(defc <next-btn> < focus-when-updated rum/static
  [props cb]
  [:button.btn.btn-default
   (assoc props
     :on-click #(cb)
     :on-key-press #(when (-> % .-charCode (= 13))
                     (cb)))
   "Next"])

(defc <note-picker> < rum/static
  [props {:keys [f-note-props]
          :or {f-note-props identity}} choose-note!]
  [:div (-> props
          (dom/add-classes
            "scale-note-picker" "text-center"))
   (->> math/all-notes (partition 6)
     (map-indexed
       (fn [i notes]
         [:div {:key (str i)}
          [:span.btn-group
           (->> notes
             (map-indexed
               (fn [i n1]
                 [:button.btn.btn-default
                  (f-note-props
                    {:key (str "note-btn-" n1)
                     :on-click #(choose-note! n1)})
                  (repr/stringify-note n1)])))]])))])

(defc <debug>
  [v]
  [:div [:pre (pr-str v)]])

(defc <notation-selector>
  < rum/static
  [props selected-notation select!]
  [:span.btn-group props
   (for [[nt nt-name] [[:m12 "Dozenal"]
                       [:solfege "solfège"]
                       [:letter "letter"]
                       ]]
     [:button.btn.btn-default
      (cond-> {:key nt-name
               :style {:width "80px"}
               :on-click (fn [_] (select! nt))}
        (= selected-notation nt) (assoc :class "active"))
      nt-name])])

(defc <height-picker>
  < rum/static
  [props
   {:as opts,
    :keys [f-props
           content]
    :or {f-props identity
         content (fn [h] (<height> h))}}
   select!
   hs]
  [:div.hpicker props
   (->> hs
     (partition-by math/octave-of-height)
     (map-indexed
       (fn [i hs]
         [:div {:key (str "row-" i)}
          (for [h hs]
            [:button.btn.btn-default.hpicker-btn
             (f-props {:key h :on-click #(select! h)} h)
             (content h)])])))
   ])
