(ns m12.widgets.gtab
  (:require [rum.core :as rum]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.utils :as u]
            [m12.widgets.guitar :as gtr])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defc <gtab> < rum/static rum/reactive
  [props
   {:as opts, :keys [n-strings length string-heights]
    :or {n-strings 6}}
   items content]
  (let [strings-items (group-by ::string items)]
    [:div.gtab
     (for [i (range n-strings)]
       [:div.gtab-string {:key (str "gtr-string-" i)}
        [:div.gtab-string-inner
         (->> (strings-items i)
           (map-indexed
             (fn [k {:as item, x ::x}]
               ;; HACK works by dirty superposition with opaque background (Val, 27 Oct 2016)
               [:div.gtab-item
                {:style {:left (str (* 100 (/ x length)) "%")}
                 :key (str "gtab-item-" k)}
                (content item i)]
               )))]
        (when-let [h (get string-heights i)]
          [:div.gtab-item.gtab-string-height
           [:div.gtab-note (math/stringify-height h)]])])]))

(defcard gtab-smoke-on-the-water
  (<gtab> {} {:length 4}
    [{::string 3 ::x 0 :height (math/parse-height "32")}
     {::string 2 ::x 0 :height (math/parse-height "37")}
     {::string 3 ::x 1 :height (math/parse-height "35")}
     {::string 2 ::x 1 :height (math/parse-height "3a")}
     {::string 3 ::x 2 :height (math/parse-height "57")}
     {::string 2 ::x 2 :height (math/parse-height "40")}]
    (fn [{:keys [height]} _]
      [:div.gtab-note (math/stringify-note height)])))

(defcard gtab-seven-nation-army
  (let [a (u/rlatom ::ex (constantly :m12))]
    (<gtab> {} {:length 8 :string-heights (gtr/standard-guitar-strings)}
      (for [[hs x] (partition 2 ["34" 0 "34" 1.5 "37" 2 "34" 2.67 "32" 3.33 "30" 4 "2b" 6])]
        {::string 4 ::x x :height (math/parse-height hs)})
      (fn [{:keys [height]} i]
        [:div.gtab-note
         (case (rum/react a)
           :m12 (math/stringify-height height)
           :classic (str (- height (get (gtr/standard-guitar-strings) i))))
         ]
        ))))