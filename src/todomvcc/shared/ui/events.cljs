(ns todomvcc.shared.ui.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]))

(rf/reg-event-db
 ::init-db
 (fn [_db _dispatch-args]
   {}))

(rf/reg-event-db
 ::set-target-db
 (fn [db [_signal target]]
   (assoc db :target-db target)))

(rf/reg-event-db
 ::set-arg
 (fn [db [_signal arg val]]
   (if (nil? val)
     (update-in db [:args] dissoc arg)
     (assoc-in db [:args arg] val))))

(rf/reg-event-fx
 ::display-results
 (fn [{:keys [db]} [_signal results]]
   (update db :query-results conj results)))
 

(rf/reg-event-fx
 ::list-todos 
  (fn [{:keys [db]} _event]
    {:http-xhrio {:method :get
                  :uri (str "https://localhost:3000/" (:target-db db) "/list-todos")
                  :response-format (ajax/json-response-format {:keywords? true})
                  :format          (ajax/json-request-format)
                  :on-success [::display-results]
                  :on-failure [::query-error]}}))

(rf/reg-event-fx
 ::list-active-todos
 (fn [{:keys [db]} _event]
   {:http-xhrio {:method :get
                 :uri (str "https://localhost:3000/" (:target-db db) "/list-active-todos")
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format          (ajax/json-request-format)
                 :on-success [:onb/display-results]
                 :on-failure [:onb/query-error]}}))

(rf/reg-event-fx
 ::list-completed-todos
 (fn [{:keys [db]} _event]
   {:http-xhrio {:method :get
                 :uri (str "https://localhost:3000/" (:target-db db) "/list-completed-todos")
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format          (ajax/json-request-format)
                 :on-success [:onb/display-results]
                 :on-failure [:onb/query-error]}}))