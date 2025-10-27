(ns todomvcc.shared.ui.subs
  (:require[re-frame.core :as rf]))

(rf/reg-sub
 ::query-results
 (fn [db _]
   (:query-results db)))