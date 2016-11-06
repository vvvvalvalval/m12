(ns m12.widgets.games.exS
  "S<x>: adding scale notes"
  (:require [m12.lib.games :as games]
            [m12.lib.math :as math]
            [m12.widgets.ui-toolkit :as utk]
            [m12.widgets.arithmetic :as warith]
            [rum.core :as rum]
            [m12.lib.games.components :as gamec]
            [m12.lib.representations :as repr]
            [m12.widgets.scale-cycle :as scyc]
            [sablono.core :as sab])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

;; ------------------------------------------------------------------------------
;; Generic view

(defc <add-scale-notes-view>
  < rum/static
  [props {:as problem, :keys [na op nb]}
   answered correct? choose! next!]
  (into [:div.text-center props]
    (concat
      (cond
        (and (= na 0) (= op :-))
        [[:strong "-" (utk/<note> nb)]]

        :else
        [[:strong (utk/<note> na)]
         (warith/span-margin (case op :+ "+" :- "-"))
         (utk/<note> nb)])
      [(warith/span-margin "=") [:strong (warith/span-margin "?")]
       [:br]
       (utk/<note-picker> {:class "text-center"} {} choose!)
       [:br]
       (cond
         correct?
         [:div [:em.m12-text-spaced "Correct!"] (utk/<next-btn> {:class "m12-text-spaced"} next!)]
         answered
         [:div [:em "Wrong!"]])])))

(defcard <add-scale-notes-view>-negative
  (<add-scale-notes-view>
    {} {:na 0 :op :- :nb 10}
    nil nil #(do nil) #(do nil)))

(defcard game-scale-note
  (let [game (games/simple-random-game
               {:generate-problem (fn [] (rand-nth [{:na 3 :op :+ :nb 7}
                                                    {:na 1 :op :- :nb 2}]))
                :get-the-answer (fn [{:keys [na op nb]}]
                                  ((case op :+ math/+n :- math/-n) na nb))})]
    (gamec/<game-in-rlatom> game ::game-scale-note1
      (fn [_ problem sa correct? submit! next!]
        (<add-scale-notes-view> {} problem sa correct? submit! next!)))))



;; ------------------------------------------------------------------------------
;; Game utils

(defn s-get-answer
  [{:as pb, :keys [na op nb]}]
  ((case op :+ math/+n :- math/-n) na nb))

;; ------------------------------------------------------------------------------

(def s1
  "S1: generic addition of scale notes"
  (games/simple-random-game
    {:generate-problem
     (fn []
       {:na (rand-nth math/all-notes)
        :op (rand-nth [:+ :-])
        :nb (rand-nth math/all-notes)})
     :get-the-answer
     s-get-answer}))

;; ------------------------------------------------------------------------------

(def s2
  "S2: find the complement."
  (games/simple-random-game
    {:generate-problem
     (fn []
       {:na 0
        :op :-
        :nb (rand-nth math/all-notes)})
     :get-the-answer
     s-get-answer}))

;; ------------------------------------------------------------------------------

(defn s3-game
  "S3: find in cycle."
  [hop]
  (let [nbs (->> math/all-notes
              (filter #(-> % (mod hop) (= 0)))
              (remove #{0})
              vec)]
    (games/simple-random-game
      {:generate-problem
       (fn [pb]
         {:na (rand-nth math/all-notes)
          :op (rand-nth [:+ :-])
          :nb (rand-nth nbs)})
       :get-the-answer
       s-get-answer})))

(def s3a
  "S3a: the cycles of 4s."
  (s3-game 4))

(def s3b
  "S3b: the cycles of 3s"
  (s3-game 3))


(defc <s3-help>
  < rum/static
  [colors]
  [:div.row
   [:div.col-sm-6.col-md-3.col-md-offset-3 {:style {:height "220px"}}
    [:div {:style {:height "100%"
                   :display "flex"
                   :flex-direction "column"
                   :justify-content "center"}}
     [:table.table
      [:tbody
       (->> math/all-notes (partition (count colors))
         (map (fn [notes]
                [:tr {:key (first notes)}
                 (->> notes
                   (map (fn [color n]
                          [:td {:key n :style {:backgroundColor color
                                               :border "1px black solid"}}
                           (repr/stringify-note n)]) colors))])))]]]
    ]
   [:div.col-sm-6.col-md-3 {:style {:height "220px"}}
    [:div {:style {:height "100%"
                   :display "flex"
                   :flex-direction "column"
                   :justify-content "center"}}
     [:div {:style {:margin "auto"}}
      (scyc/scale-cycle {:style {:display "inline-block"}}
        {:width 200
         :f-note-props (fn [props note _]
                         (assoc props :fill (colors (mod note (count colors)))))})]]]])

(defn <s3-view> [colors]
  (fn
    [props
     problem answered correct?
     choose! next!]
    (sab/html
      [:div.text-center props
       (<s3-help> colors)
       (<add-scale-notes-view> props
         problem answered correct?
         choose! next!)])))

(def ^:private s3a-colors ["#B2FF59" "#69F0AE" "#64FFDA" "#18FFFF"])
(def ^:private s3a-view (<s3-view> s3a-colors))

(defc <s3a>
  < rum/static
  [props
   problem answered correct?
   choose! next!]
  (s3a-view props
    problem answered correct?
    choose! next!))

(def ^:private s3b-colors ["#FFFF00" "#FFD740" "#FFCC80"])
(def ^:private s3b-view (<s3-view> s3b-colors))

(defc <s3b>
  < rum/static
  [props
   problem answered correct?
   choose! next!]
  (s3b-view props
    problem answered correct?
    choose! next!))



