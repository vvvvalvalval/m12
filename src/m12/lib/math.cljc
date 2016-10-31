(ns m12.lib.math
  "Current notation is to write a height as an integer in base 12."
  (:require [clojure.string :as str]))

;; TODO

(defn from-base-12
  "parses an int in base 12"
  [s]
  #?(:clj (Long/parseLong s 12)
     :cljs (js/parseInt s 12)))

(defn to-base-12
  "Writes an int in base 12."
  [x]
  #?(:clj (Long/toString x 12)
     :cljs (.toString x 12)))

;; TODO maybe we'll want to do things differently for negative intervals.
;; e.g maybe -17 (as in -10 + 7) is clearer than -5
;; (Val, 23 Oct 2016)

(defn note-of-height
  "Extracts the note (in Z12) of a height (in Z)"
  [h]
  (-> h (mod 12)))

(defn octave-of-height
  [h]
  (-> h (quot 12)))

;; ------------------------------------------------------------------------
;; heights (i.e in Z)

(def all-heights
  (vec (range (from-base-12 "10") (from-base-12 "90"))))


;; ------------------------------------------------------------------------
;; Notes (i.e in Z12)

(def all-notes
  "The whole chromatic scales"
  (vec (range 12)))

(defn +n
  [& notes]
  (-> (apply + notes) (mod 12)))

(defn -n
  [& notes]
  (-> (apply - notes) (mod 12)))

;; ------------------------------------------------------------------------
;; Note Conversions
