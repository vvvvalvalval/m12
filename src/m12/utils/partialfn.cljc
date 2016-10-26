(ns m12.utils.partialfn)

#?(
:cljs
(defrecord PartialFn [f args]
  cljs.core/IFn
  (-invoke [_ & xs] (apply f (concat args xs))))

:clj
(defrecord PartialFn [f args]
  clojure.lang.IFn
  (invoke [_ & xs] (apply f (concat args xs))))
)


