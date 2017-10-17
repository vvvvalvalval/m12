(ns m12.core
  (:require
    [clojure.string :as str]
    [sablono.core :as sab :include-macros true]
    [rum.core :as rum]

    [m12.widgets.notation]
    [m12.widgets.arithmetic]
    [m12.widgets.guitar]
    [m12.widgets.gtab]
    [m12.widgets.piano]
    [m12.widgets.piano-widgets]
    [m12.widgets.scale-cycle]
    [m12.widgets.posts]
    [m12.lib.games]
    [m12.widgets.games.exG2]

    [m12.pages.welcome]
    [sc.api])
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]
    [sc.api :refer [spy defsc letsc]]))

(enable-console-print!)

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (m12.pages.welcome/<welcome>) node)))

(main)

