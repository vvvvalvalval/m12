(ns m12.widgets.arithmetic
  (:require [rum.core :as rum]
            [sablono.core :as sab :include-macros true]

            [m12.services.synth :as synth]
            [m12.lib.math :as math]
            [m12.lib.representations :as repr]
            [m12.widgets.ui-toolkit :as utk]
            [clojure.string :as str]
            [m12.utils :as u])
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
;; Complement

(defn fc-init []
  {:n (rand-nth math/all-notes)
   :answered nil})

(defc <find-complement>
  < rum/reactive
  [state]
  (let [{:keys [n answered]} (rum/react state)
        next! #(reset! state (fc-init))
        correct? (and answered (= answered (math/-n n)))
        submit! #(swap! state assoc :answered %)]
    [:div.text-center
     [:p "Find the complement of the given interval note:"]
     [:div
      (utk/<note> n)
      (span-margin "= - ?")
      [:form {:on-submit #(when correct? (next!))}
       (for [n2 math/all-notes]
         [:button.btn.btn-default
          {:key (str n2)
           :on-click #(submit! n2)}
          (repr/stringify-note n2)])]
      (when answered
        [:span
         (if correct?
           "Well played!"
           "Nope, try again.")
         (span-margin [:button.btn.btn-default {:on-click #(next!)}
                       "Next"])])
      ]]))

(defcard <find-complement>
  (<find-complement> (u/rlatom ::fc1 fc-init)))

;; ------------------------------------------------------------------------
;; tables

(defc <notes-op-table>
  [op op-name]
  (let [cell-side "30px"
        cell-style {:height cell-side :width cell-side
                    :text-align "center"
                    :border "1px solid"}]
    [:table
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
   [:div.col-md-6
    [:h4 "Note addition"]
    (<notes-op-table> math/+n "+")]
   [:div.col-md-6
    [:h4 "Note substraction"]
    (<notes-op-table> math/-n "-")]])

(defcard <op-tables>
  (<op-tables>))

;; ------------------------------------------------------------------------------
;; Add interval

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
         (span-margin (case op :+ "+" :- "-"))
         (utk/<note> nb)])
      [(span-margin "=") [:strong (span-margin "?")]
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


(defn add-scale-notes-choose!
  [a answer]
  (swap! a update :game/state
    assoc :answered answer))

(defn add-scale-notes-next!
  [next-problem a]
  (swap! a (fn [state]
             (-> state
               (assoc :game/state {:answered nil})
               (update :game/problem next-problem)))))

(defc <add-scale-notes-game>
  < rum/static rum/reactive
  [props a next-problem]
  (let [{problem :game/problem
         game-state :game/state} (rum/react a)
        {:keys [na op nb]} problem

        solution ((case op :+ math/+n :- math/-n)
                   na nb)
        {:keys [answered]} game-state
        correct? (= answered solution)

        choose! (u/pfn add-scale-notes-choose! a)
        next! (u/pfn add-scale-notes-next! next-problem a)]
    (<add-scale-notes-view> props
      problem answered correct?
      choose! next!)
    ))

(defn add-scale-notes-init
  [na op nb]
  {:game/problem {:na na :op op :nb nb}
   :game/state {:answered nil}})

(defcard <add-scale-notes-game>-1
  "This one is deterministic: Fibonacci-like"
  (<add-scale-notes-game> {}
    (u/rlatom ::game1 (constantly (add-scale-notes-init 2 :+ 3)))
    (fn [{:as old-problem, :keys [na op nb]}]
      (let [new-op (case op :+ :- :- :+)]
        {:na nb
         :op new-op
         :nb ((case new-op :+ math/+n :- math/-n)
               na nb)}))
    ))

;; ------------------------------------------------------------------------------
;; Add fixed interval

(def add-interval-intervals
  (vec (reverse (range -11 12))))

(defn add-interval-next-problem
  [+int problem]
  {:na (rand-nth math/all-notes)
   :op (if (< +int 0) :- :+)
   :nb (.abs js/Math +int)})

(defn add-fixed-interval-reset+int!
  [state +int]
  (swap! state assoc :+int +int))

(defc <add-fixed-interval>
  < rum/reactive
  [state]
  (let [{:keys [+int]} (rum/react state)]
    [:div {}
     (utk/select {:class "pull-right"}
       {::utk/from-value (fn [s] (js/parseInt s))
        ::utk/option-text (fn [v]
                            (str (if (< v 0) "-" "+")
                              (repr/stringify-note (.abs js/Math v))))}
       +int (u/pfn add-fixed-interval-reset+int! state)
       add-interval-intervals)
     (<add-scale-notes-game> {}
       state (u/pfn add-interval-next-problem +int))
     ]))

(defcard <add-fixed-interval>-ex
  (<add-fixed-interval>
    (u/rlatom ::afi2
      (constantly {:+int 7
                   :game/problem {:na 3 :op :+ :nb 7}
                   :game/state {:answered nil}}))))
