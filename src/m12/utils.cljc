(ns m12.utils
  (:require [rum.core :as rum]
            [m12.utils.partialfn]
            [sc.api]
            [sc.api.logging]))

(defn toggle-conj
  "Given a set s and an element x, adds x to s if not present, and removes it if present.

  Mathematically: (s,x) -> s ∆ {x}"
  [s x]
  ((if (get s x) disj conj) s x))

(defonce ^:private all-glatoms (atom {}))

(defn rlatom
  "'ReLoadable ATOM': dev utility for obtaining a 'reloadable atom'.
  Given a global name and an optional 0-arity init-fn,
  will return a Rum cursor which value survives reloads. Especially useful in devcards."
  ([name] (rlatom name #(do nil)))
  ([name init-fn]
   (when-not (contains? @all-glatoms name)
     (swap! all-glatoms #(assoc % name (init-fn))))
   (rum/cursor all-glatoms name)))

(defn reset-glatom! [name v]
  (reset! (rlatom name) v))

(defn pfn
  "Partial function application as a data structure,
  which therefore has equality semantics, at the expense of executing more slowly.

  Useful for creating callbacks that you pass down to rum.core/static components."
  [f & first-args]
  (m12.utils.partialfn/->PartialFn f (vec first-args)))

(sc.api.logging/register-cs-logger
  ::dummy-logger
  (fn [cs-data]
    (binding [*out* (java.io.OutputStreamWriter. System/out)]
      (sc.api.logging/log-cs "SPY" cs-data))))

(def my-spy-opts
  `{:sc/spy-cs-logger-id ::dummy-logger})

(defmacro spy
  ([] (sc.api/spy-emit my-spy-opts nil &env &form))
  ([expr] (sc.api/spy-emit my-spy-opts expr &env &form))
  ([opts expr] (sc.api/spy-emit (merge my-spy-opts opts) expr &env &form)))

