(ns m12.utils.dom
  (:require [clojure.string :as str]))

(defn focus [dom-node]
  #?(:cljs (.focus dom-node)))

(defn add-classes
  "Adds CSS classes"
  [props & classes]
  (update props :class #(str % " " (str/join classes " "))))