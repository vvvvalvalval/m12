(ns m12.utils.svg
  (:require [m12.utils.geometry :as g :refer [v+ v- v* vx vy]]))

(defn M [[x y]]
  (str "M " x " " y))

(defn C [ctrl1 ctrl2 dest]
  (str "C "
       (vx ctrl1) " " (vy ctrl1) " "
       (vx ctrl2) " " (vy ctrl2) " "
       (vx dest) " " (vy dest)))

(defn L [dest]
  (str "L " (vx dest) " " (vy dest)))

(defn circle-arc [r large-arc-flag sweep-flag dest]
  (str "A " r " " r " " 0 " " large-arc-flag " " sweep-flag " "
    (vx dest) " " (vy dest)))

(defn quadratic-arc
  [orig v-orig dest v-dest]
  (let [[xi yi] (g/intersection-point orig v-orig dest v-dest)
        [xd yd] dest]
    (str "Q "
      xi " " yi " " xd " " yd)))
