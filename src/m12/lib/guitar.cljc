(ns m12.lib.guitar
  (:require [clojure.string :as str]
            [m12.lib.representations :as repr]))

(defn standard-guitar-strings
  []
  (->> (str/split "24 29 32 37 3b 44" #" ")
    reverse (mapv repr/parse-height)))
