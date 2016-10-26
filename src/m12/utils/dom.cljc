(ns m12.utils.dom)

(defn focus [dom-node]
  #?(:cljs (.focus dom-node)))


