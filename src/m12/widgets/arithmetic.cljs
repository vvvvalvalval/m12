(ns m12.widgets.arithmetic
  (:require [rum.core :as rum]
            [sablono.core :as sab :include-macros true]
            [clojure.string :as str]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.lib.representations :as repr]
            [m12.widgets.ui-toolkit :as utk]
            [m12.utils :as u]

            [m12.lib.games :as games]
            [m12.lib.games.components :as gamec])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(defn fni-correct? [x z submitted]
  (= (math/+n x submitted) z))

(defn span-margin [content]
  [:span {:style {:margin "5px"}} content])

(defc <find-notes-interval-view>
  [x z answered submit! clear-submission! next! request-focus]
  (let [correct? (and answered (fni-correct? x z answered))]
    [:div
     [:p "Find the interval note between the 2 given notes:"]
     [:div
      (utk/<note> x)
      (span-margin "+")
      [:form {:style {:display "inline"}
              :on-submit #(when correct? (next!))}
       (utk/note-input {:placeholder "type in a note..."
                        :key (str x "-" z)}
         {:on-new-value! submit!
          :on-clear! clear-submission!
          :on-mounted! #(when request-focus
                         (.focus %))})]
      (span-margin "=")
      (utk/<note> z)
      (when answered
        [:span.pull-right
         (if correct?
           "Well played!"
           "Nope, try again.")
         (span-margin [:button.btn.btn-default {:on-click #(next!)}
                       "Next"])])]]))

(defn init-fni []
  {:x (rand-nth math/all-notes)
   :z (rand-nth math/all-notes)
   :answered nil})

(defcs <find-notes-interval> < (rum/local (init-fni) ::state)
  [state]
  (let [a (::state state)
        {:keys [x z answered]} @a]
    (<find-notes-interval-view> x z answered
      #(swap! a assoc :answered %)
      #(swap! a dissoc :answered)
      #(reset! a (init-fni))
      true)))


(defcard <find-notes-interval-view>
  (sab/html
    [:div
     [:h5 "no answer"]
     (<find-notes-interval-view> 4 11 nil #(do % nil) #(do nil) false)
     [:h5 "wrong answer"]
     (<find-notes-interval-view> 4 11 3 #(do % nil) #(do nil) false)
     [:h5 "good answer"]
     (<find-notes-interval-view> 4 11 7 #(do % nil) #(do nil) false)]))

(defcard <find-notes-interval>
  (<find-notes-interval>))

;; ------------------------------------------------------------------------
;; tables

(defc <notes-op-table>
  [op op-name]
  (let [cell-side "30px"
        cell-style {:height cell-side :width cell-side
                    :text-align "center"
                    :border "1px solid"}]
    [:table {:style {:margin "auto"}}
     [:tr
      [:td {:style cell-style} [:strong op-name]]
      (for [n math/all-notes]
        [:td {:key (str n) :style cell-style}
         [:strong (repr/stringify-note n)]])]
     (for [m math/all-notes]
       [:tr {:key (str m)}
        [:td {:style cell-style}
         [:strong (repr/stringify-note m)]]
        (for [n math/all-notes]
          [:td {:key (str n) :style cell-style}
           (repr/stringify-note (op m n))])])]))

(defc <op-tables> []
  [:div.row
   [:div.col-md-6.text-center
    [:h4 "Note addition"]
    (<notes-op-table> math/+n "+")]
   [:div.col-md-6.text-center
    [:h4 "Note substraction"]
    (<notes-op-table> math/-n "-")]])

(defcard <op-tables>
  (<op-tables>))
