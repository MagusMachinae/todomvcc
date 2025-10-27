(ns todomvcc.shared.ui.core
  (:require [goog.events :as events]
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf]
            [todomvcc.ui.events] ;; These two are only required to make the compiler
            [todomvcc.ui.subs]   ;; load them (see docs/App-Structure.md)
            [todomvcc.ui.views]))

(enable-console-print!)
(rf/dispatch-sync [:initialise-db])

(defonce root-container
  (rdc/create-root (.getElementById js/document "app")))

(defn render
  []
  (rdc/render root-container [todomvcc.ui.views/body]))

(defn ^:dev/after-load clear-cache-and-render!
  [] 
  (rf/clear-subscription-cache!)
  (render))

(defn ^:export main
  []
  (render))