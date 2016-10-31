(ns m12.lib.games.protocols)

(defprotocol GameGenerator
  (initial-state [this])

  (next-state [this state]))
