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

(defn batch-of-height
  [h]
  (-> h (quot 12)))

;; ------------------------------------------------------------------------
;; heights (i.e in Z)

(def all-heights
  (vec (range (from-base-12 "10") (from-base-12 "90"))))

(defn parse-height
  [h-s]
  (from-base-12 h-s))

(defn stringify-height
  [h]
  (to-base-12 h))

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

(defn stringify-note [n]
  (to-base-12 n))

(defn valid-note?
  "Validates note input"
  [n-s]
  (some? (re-matches #"\s*[0-9abAB]+\s*" n-s)))

(defn parse-note
  [n-s]
   (-> n-s from-base-12 (mod 12)))

(def classic-notes
  (vec (str/split "C C# D D# E F F# G G# A A# B" #" ")))

(defn stringify-classic-note
  [n]
  (get classic-notes n))

(defn parse-classic-note
  [n-s]
  (case n-s
    "C" 0
    "C#" 1 "Db" 1
    "D" 2
    "D#" 3 "Eb" 3
    "E" 4 "Fb" 4
    "F" 5 "E#" 5
    "F#" 6 "Gb" 6
    "G" 7
    "G#" 8 "Ab" 8
    "A" 9
    "A#" 10 "Bb" 10
    "B" 11 "Cb" 11))

(def solfege-notes
  (vec (str/split "do do# re re# mi fa fa# sol sol# la la# si" #" ")))

(defn stringify-solfege-note
  [n]
  (get solfege-notes n))

(defn parse-solfege-note
  [n-s]
  (case n-s
    "do" 0
    "do#" 1 "reb" 1
    "re" 2
    "re#" 3 "mib" 3
    "mi" 4 "fab" 4
    "fa" 5 "mi#" 5
    "fa#" 6 "solb" 6
    "sol" 7
    "sol#" 8 "lab" 8
    "la" 9
    "la#" 10 "sib" 10
    "si" 11 "dob" 11))

;; ------------------------------------------------------------------------

(defn stringify-classic-height
  [h]
  (str (stringify-classic-note (note-of-height h))
    (batch-of-height h)))

(defn stringify-solfege-height
  [h]
  (str (stringify-solfege-note (note-of-height h))
    (batch-of-height h)))
