(ns m12.core
  (:require
    [clojure.string :as str]
    [sablono.core :as sab :include-macros true]

    [rum.core :as rum]
    [m12.services.synth :as synth]

    [m12.widgets.notation])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defcard first-card
  (sab/html [:div
             [:h1 "This is your first devcard!"]]))

(def note-literals (vec (str/split "C C# D D# E F F# G G# A A# B" #" ")))

(defn hr-height [height]
  (str (get note-literals (mod height 12))
    (quot height 12)))

(defn <play-notes-button> [notes]
  (sab/html
    [:button {:on-click (fn [_] (synth/play notes) :done)}
     (str "Play " (str/join "-" (map #(hr-height (:height %)) notes)))]
    ))

(defcard play-C-3
  (<play-notes-button> [{:sound "piano" :duration 1 :height (+ (* 12 3) 0)}]))

(defn basic-major-cord
  [fundamental]
  (->> [0 4 7]
    (mapv #(update fundamental :height + %))))

(defcard play-C-chord
  (<play-notes-button> (basic-major-cord {:sound "piano" :duration 1 :height (+ (* 12 3) 0)}))
  )

(defc <hello> < rum/reactive [greetee a-counter]
  [:div
   [:p "Hello " greetee]
   [:div "You have cliked"
    [:button {:on-click #(swap! a-counter inc)}
     (rum/react a-counter)] "times"]])

(defcard rum-pg (<hello> "world!" (atom 0)))

(defc <higher-order> [c]
  [:div
   [:h3 "wrapping"]
   [:div (c)]])

(defonce counter (atom 0))
(defcard rum-hoc (<higher-order> #(<hello> "Val" counter)))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div "This is SO working"]) node)))

(main)



;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

