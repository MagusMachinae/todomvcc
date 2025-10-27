(ns todomvcc.shared.ui.core
  (:require [goog.events :as g-events]
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf]
            [todomvcc.shared.ui.events :as events] 
            [todomvcc.shared.ui.subs :as subs]   
            [todomvcc.shared.ui.view :as view]))

(enable-console-print!)
(rf/dispatch-sync [:initialise-db])

(defonce root-container
  (rdc/create-root (.getElementById js/document "app")))

(defn render
  []
  (rdc/render root-container [view/body]))

(defn ^:dev/after-load clear-cache-and-render!
  [] 
  (rf/clear-subscription-cache!)
  (render))

(defn ^:export main
  []
  (render))