(ns re-streamer.stateful-stream
  #?(:cljs (:require [reagent.core :as reagent]))
  (:refer-clojure :rename {map c-map distinct c-distinct filter c-filter}))

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
      :destroy!     (fn []
                      (remove-watch state :watch)
                      nil)
      :state        state})))

;; watcher keys generator

(defonce ^:private watcher-key
         (let [counter (atom 0)]
           #(swap! counter inc)))

;; operators

(defn map
  ([stream f]
   (map stream f (watcher-key)))
  ([stream f watcher-key]
   (let [state #?(:cljs    (reagent/atom (f @(:state stream)))
                  :default (atom (f @(:state stream))))
         subs (atom #{})]

     (add-watch (:state stream) watcher-key #(reset! state (f %4)))
     (add-watch state :watch #(doseq [sub @subs] (sub %4)))

     {:subscribe!   (fn [sub]
                      (swap! subs conj sub)
                      (sub @state)
                      sub)
      :unsubscribe! (fn [sub]
                      (swap! subs disj sub)
                      nil)
      :flush!       (fn []
                      (reset! subs #{})
                      nil)
      :destroy!     (fn []
                      (remove-watch (:state stream) watcher-key)
                      (remove-watch state :watch)
                      nil)
      :state        state})))

(defn pluck
  ([stream keys]
   (pluck stream keys (watcher-key)))
  ([stream keys watcher-key]
   (map stream #(select-keys % keys) watcher-key)))

(defn distinct
  ([stream f]
   (distinct stream f (watcher-key)))
  ([stream f watcher-key]
   (let [state #?(:cljs    (reagent/atom @(:state stream))
                  :default (atom @(:state stream)))
         subs (atom #{})]

     (add-watch (:state stream) watcher-key #(if (not (f @state %4)) (reset! state %4)))
     (add-watch state :watch #(doseq [sub @subs] (sub %4)))

     {:subscribe!   (fn [sub]
                      (swap! subs conj sub)
                      (sub @state)
                      sub)
      :unsubscribe! (fn [sub]
                      (swap! subs disj sub)
                      nil)
      :flush!       (fn []
                      (reset! subs #{})
                      nil)
      :destroy!     (fn []
                      (remove-watch (:state stream) watcher-key)
                      (remove-watch state :watch)
                      nil)
      :state        state})))

(defn filter
  ([stream f]
   (filter stream f (watcher-key)))
  ([stream f watcher-key]
   (let [state #?(:cljs    (reagent/atom (if (f @(:state stream)) @(:state stream) nil))
                  :default (atom (if (f @(:state stream)) @(:state stream) nil)))
         subs (atom #{})]

     (add-watch (:state stream) watcher-key #(if (f %4) (reset! state %4)))
     (add-watch state :watch #(doseq [sub @subs] (sub %4)))

     {:subscribe!   (fn [sub]
                      (swap! subs conj sub)
                      (if (f @(:state stream)) (sub @state))
                      sub)
      :unsubscribe! (fn [sub]
                      (swap! subs disj sub)
                      nil)
      :flush!       (fn []
                      (reset! subs #{})
                      nil)
      :destroy!     (fn []
                      (remove-watch (:state stream) watcher-key)
                      (remove-watch state :watch)
                      nil)
      :state        state})))
