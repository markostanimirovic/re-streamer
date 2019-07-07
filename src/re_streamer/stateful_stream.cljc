(ns re-streamer.stateful-stream
  #?(:cljs (:require [reagent.core :as reagent]))
  (:refer-clojure :rename {map c-map distinct c-distinct}))

(defn create
  ([] (create nil))
  ([val]
   (let [subs (atom #{})
         state #?(:cljs    (reagent/atom val)
                  :default (atom val))]

     (add-watch state :watch #(doseq [sub @subs] (sub %4)))

     {:subscribe!   (fn [sub]
                      (swap! subs conj sub)
                      (sub @state)
                      sub)
      :unsubscribe! (fn [sub]
                      (swap! subs disj sub)
                      nil)
      :emit!        (fn [val]
                      (reset! state val)
                      nil)
      :flush!       (fn []
                      (reset! subs #{})
                      nil)
      :state        state})))

;; operators

(defn map [stream f]
  (let [state #?(:cljs    (reagent/atom (f @(:state stream)))
                 :default (atom (f @(:state stream))))
        subs (atom #{})]

    (add-watch (:state stream) :watch #(reset! state (f %4)))
    (add-watch state :watch #(doseq [sub @subs] (sub %4)))

    {:subscribe! (fn [sub]
                   (swap! subs conj sub)
                   (sub @state))
     :state      state}))

(defn pluck [stream keys]
  (map stream #(select-keys % keys)))

(defn distinct [stream f]
  (let [state #?(:cljs    (reagent/atom @(:state stream))
                 :default (atom @(:state stream)))
        subs (atom #{})]

    (add-watch (:state stream) :watch #(if (not (f @state %4)) (reset! state %4)))
    (add-watch state :watch #(doseq [sub @subs] (sub %4)))

    {:subscribe! (fn [sub]
                   (swap! subs conj sub)
                   (sub @state))
     :state      state}))
