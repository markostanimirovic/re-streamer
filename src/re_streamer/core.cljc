(ns re-streamer.core
  (:require [re-streamer.types :as types]
            #?(:cljs [reagent.core :as reagent]))
  (:refer-clojure :rename {map c-map distinct c-distinct filter c-filter}))

;; === factories ===

(defn create-stream
  ([] (create-stream nil))
  ([val] (let [subs (atom #{})
               state #?(:cljs    (reagent/atom val)
                        :default (atom val))]

           (add-watch state :state-watcher #(doseq [sub @subs] (sub %4)))

           (types/->Stream subs state))))

(defn create-behavior-stream
  ([] (create-behavior-stream nil))
  ([val]
   (let [subs (atom #{})
         state #?(:cljs    (reagent/atom val)
                  :default (atom val))]

     (add-watch state :state-watcher #(doseq [sub @subs] (sub %4)))

     (types/->BehaviorStream subs state))))

(defmulti create-subscriber (fn [stream _ _ _] (type stream)))

(defmethod create-subscriber ::subscriber [stream watcher-key subs state]
  (types/->Subscriber subs state {:state (:state stream) :watcher-key watcher-key}))

(defmethod create-subscriber ::behavior-subscriber [stream watcher-key subs state]
  (types/->BehaviorSubscriber subs state {:state (:state stream) :watcher-key watcher-key}))

(defmulti create-filtered-subscriber (fn [stream _ _ _ _] (type stream)))

(defmethod create-filtered-subscriber ::subscriber [stream _ watcher-key subs state]
  (create-subscriber stream watcher-key subs state))

(defmethod create-filtered-subscriber ::behavior-subscriber [stream filter watcher-key subs state]
  (types/->BehaviorFilteredSubscriber subs state {:state (:state stream) :watcher-key watcher-key} filter))

;; === watcher keys generator ===

(defonce ^:private watcher-key
         (let [counter (atom 0)]
           #(swap! counter inc)))

;; === operators ===

(defn map
  ([stream f]
   (map stream f (watcher-key)))
  ([stream f watcher-key]
   (let [state #?(:cljs    (reagent/atom (f @(:state stream)))
                  :default (atom (f @(:state stream))))
         subs (atom #{})]

     (add-watch (:state stream) watcher-key #(reset! state (f %4)))
     (add-watch state :watch #(doseq [sub @subs] (sub %4)))

     (create-subscriber stream watcher-key subs state))))

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

     (create-subscriber stream watcher-key subs state))))

(defn filter
  ([stream f]
   (filter stream f (watcher-key)))
  ([stream f watcher-key]
   (let [state #?(:cljs    (reagent/atom (if (f @(:state stream)) @(:state stream) nil))
                  :default (atom (if (f @(:state stream)) @(:state stream) nil)))
         subs (atom #{})]

     (add-watch (:state stream) watcher-key #(if (f %4) (reset! state %4)))
     (add-watch state :watch #(doseq [sub @subs] (sub %4)))

     (create-filtered-subscriber stream f watcher-key subs state))))
