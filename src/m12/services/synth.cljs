(ns m12.services.synth
  "Integration with the AudioSynth library.

  NOTE: no volume control."
  (:require [m12.utils :refer-macros [spy]]
            [sc.api :refer-macros [letsc defsc]]))

;; ------------------------------------------------------------------------
;; AudioSynth

(defn as-play
  [sound note octave duration]
  ;; TODO externs (Val, 23 Oct 2016)
  (.play (aget js/window "Synth") sound note octave duration))

(defn as-note
  [height]
  (case (mod height 12)
    0 "C" 1 "C#" 2 "D" 3 "D#" 4 "E" 5 "F" 6 "F#" 7 "G" 8 "G#" 9 "A" 10 "A#" 11 "B"))

(defn as-octave
  [height]
  (-> height (quot 12)))

;; ------------------------------------------------------------------------
;; API

(defn play-note
  [{:as note, :keys [sound height duration]}]
  (as-play sound (spy (as-note height)) (as-octave height) duration))

(defn play [notes]
  (run! play-note notes))




