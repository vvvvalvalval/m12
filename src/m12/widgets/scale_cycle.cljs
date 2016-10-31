(ns m12.widgets.scale-cycle
  (:require [sablono.core :as sab]
            [m12.utils.geometry :as g :refer [vx vy v* v+ v- u> v>]]
            [m12.utils.svg :as svg]
            [rum.core :as rum]
            [m12.lib.math :as math]
            [m12.services.synth :as synth])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]])
  )

(defcard svg-circle
  (sab/html
    [:svg {:width "100" :height "100"}
     [:circle {:cx     50 :cy "50" :r "40"
               :stroke "green" :stroke-width "4"
               :fill   "yellow"}]]))

;; cycle

(defn F [theta phi t]
  (v* t (u> (+ theta (* t phi))))
  )

(defn dF|dt [theta phi t]
  (v+
    (u> (+ theta (* t phi)))
    (v* (* t phi) (v> (+ theta (* t phi))))
    ))

(defn i->note [i]
  (mod (- i) 12))

(defc scale-quadrant
  < rum/static
  [theta0 t0 t1 phi
   center r
   i f-note-props note-data]
  (let [theta (+ theta0 (/ (* i 2 g/PI) 12))
        theta+1 (+ theta (/ (* 2 g/PI) 12))

        p0 (v+ center (v* r (F theta phi t0)))
        v0 (dF|dt theta phi t0)
        p1 (v+ center (v* r (F theta phi t1)))
        v1 (dF|dt theta phi t1)
        p2 (v+ center (v* r (F theta+1 phi t1)))
        v2 (dF|dt theta+1 phi t1)
        p3 (v+ center (v* r (F theta+1 phi t0)))
        v3 (dF|dt theta+1 phi t0)]
    [:path.scale-cycle-quadrant
     (f-note-props
       {:key (str "arc-" i)
        :d (str
             (svg/M p0) " "
             (svg/quadratic-arc p0 v0 p1 v1)
             " "
             (svg/circle-arc (* r t1) 0 1 p2)
             " "
             (svg/quadratic-arc p2 v2 p3 v3)
             " "
             (svg/circle-arc (* r t0) 0 0 p0)
             )
        :stroke "black" :stroke-width 1
        :fill "transparent"}
       (i->note i) note-data)]
    ))

(defc note-text
  < rum/static
  [theta0 phi center r i]
  (let [note (i->note i)
        theta (+ theta0 (/ (* (+ i 0.5) 2 g/PI) 12))
        [x y] (-> center
                (v+ (v* r (F theta phi 0.87)))
                (v- [5 -5]))]
    [:text.scale-cycle-text
     {:x x :y y :key (str "note-text-" i)}
     (math/stringify-note note)]))

(def phi
  "The angle (in radians) which determines how curly the cycle looks."
  -0.3 )
(def theta0
  "The start angle."
  (+ phi (/ g/PI 12)))

(def t0
  "Relative radius of the inner circle"
  0.0)
(def t1
  "Relative radius of the outer circle"
  1.0)

(def default-f-note-props
  (fn [props n] props))

(def default-note-data
  (fn [note] nil))

(defc scale-cycle
  < rum/static
  [props {:keys [center width style
                 note-data
                 f-note-props]
          :or {style ""
               f-note-props default-f-note-props
               note-data default-note-data}}]
  (let [r (/ width 2)
        r2 (- r 1)
        center2 [r r]]
    [:svg (assoc props
            :x (- (vx center) r) :y (- (vy center) r)
            :width (+ (vx center) r) :height (+ (vy center) r))
     [:style style]
     (for [i (range 12)]
       (let [note (i->note i)]
         (scale-quadrant theta0 t0 t1 phi
           center2 r2
           i f-note-props (note-data note))))
     (for [i (range 12)]
       (note-text theta0 phi center2 r2 i))]
    ))

;; IMPROVEMENT have a :note-data optional key in the
(defcard cycle-ex
  (scale-cycle {}
    {:center [100 100] :width 200
     :style ".cycle-ex-quadrant:hover {fill: orange; cursor: pointer;}
       //.scale-cycle-text {font-weight: bold;}"
     :f-note-props
     (let [base-height (math/parse-height "40")]
       (fn [props note _]
         (assoc props
           :class "cycle-ex-quadrant"
           :on-click #(synth/play
                       [{:sound "piano" :duration 2
                         :height (+ base-height note)}]))))}))

(defcard cycle-notes-set
  (let [notes-set #{0 4 7}]
    (scale-cycle {}
      {:center [100 100] :width 200
       :style ".note-in-set {fill: chartreuse;}"
       :note-data (reduce
                    (fn [m note]
                      (assoc m note (notes-set note)))
                    {} math/all-notes)
       :f-note-props
       (let [base-height (math/parse-height "40")]
         (fn [props note in-set?]
           (cond-> props
             in-set? (assoc :class "note-in-set"))))})))







