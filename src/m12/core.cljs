(ns m12.core
  (:require
    [clojure.string :as str]
    [sablono.core :as sab :include-macros true]
    [rum.core :as rum]

    [m12.widgets.notation]
    [m12.widgets.arithmetic]
    [m12.widgets.guitar]
    [m12.widgets.gtab]
    )
  (:require-macros
    [rum.core :as rum :refer [defc defcs]]
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defcard first-card
  (sab/html [:div
             [:h1 "This is your first devcard!"]]))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div "This is SO working"]) node)))

(main)

