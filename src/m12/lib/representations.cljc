(ns m12.lib.representations
  (:require [m12.lib.math :as math]
            [clojure.string :as str]))

(defn parse-height
  [h-s]
  (math/from-base-12 h-s))

(def ph parse-height)

(defn stringify-height
  [h]
  (math/to-base-12 h))

(def sh stringify-height)

(defn stringify-note [n]
  (math/to-base-12 n))

(def sno stringify-note)

(defn valid-note?
  "Validates note input"
  [n-s]
  (some? (re-matches #"\s*[0-9abAB]+\s*" n-s)))

(defn parse-note
  [n-s]
  (-> n-s math/from-base-12 (mod 12)))

(def pno parse-note)

(def classic-notes
  (vec (str/split "C C# D D# E F F# G G# A A# B" #" ")))

(defn stringify-letter-note
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

(defn stringify-letter-height
  [h]
  (str (stringify-letter-note (math/note-of-height h))
    (math/octave-of-height h)))

(defn stringify-solfege-height
  [h]
  (str (stringify-solfege-note (math/note-of-height h))
    (math/octave-of-height h)))

