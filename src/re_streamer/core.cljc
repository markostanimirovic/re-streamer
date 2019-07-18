(ns re-streamer.core
  #?(:cljs (:require [reagent.core :as reagent]))
  (:refer-clojure :rename {map c-map distinct c-distinct filter c-filter flush c-flush}))

;; === Types ===

(defprotocol Emitable
  (emit [this val])
  (flush [this]))

;; === Streams ===

(defrecord Stream [subs state])

(extend-type Stream
  Emitable
  (emit [this val] (reset! (:state this) val))
  (flush [this] (reset! (:subs this) #{})))

(defrecord BehaviorStream [subs state])

(extend-type BehaviorStream
  Emitable
  (emit [this val] (reset! (:state this) val))
  (flush [this] (reset! (:subs this) #{})))

;; === Subscribers ===

(defrecord Subscriber [subs state parent])

(defrecord BehaviorSubscriber [subs state parent])

(defrecord BehaviorFilteredSubscriber [subs state parent filter])

;; === Subscribable Methods ===

(defn unsubscribe [this sub] (swap! (:subs this) disj sub))

(defmulti subscribe (fn [this _] (type this)))

(defn- stream-subscribe [this sub]
  (swap! (:subs this) conj sub)
  sub)

(defmethod subscribe Stream [this sub]
  (stream-subscribe this sub))

(defmethod subscribe Subscriber [this sub]
  (stream-subscribe this sub))

(defn- behavior-stream-subscribe [this sub]
  (swap! (:subs this) conj sub)
  (sub @(:state this))
  sub)

(defmethod subscribe BehaviorStream [this sub]
  (behavior-stream-subscribe this sub))

(defmethod subscribe BehaviorSubscriber [this sub]
  (behavior-stream-subscribe this sub))

(defmethod subscribe BehaviorFilteredSubscriber [this sub]
  (swap! (:subs this) conj sub)
  (if ((:filter this) @(:state (:parent this)))
    (sub @(:state this)))
  sub)

(defmulti destroy (fn [this] (type this)))

(defn- stream-destroy [this]
  (remove-watch (:state this) :state-watcher))

(defmethod destroy Stream [this]
  (stream-destroy this))

(defmethod destroy BehaviorStream [this]
  (stream-destroy this))

(defn- subscriber-destroy [this]
  (remove-watch (:state this) :state-watcher)
  (remove-watch (:state (:parent this)) (:watcher-key (:parent this))))


(defmethod destroy Subscriber [this]
  (subscriber-destroy this))

(defmethod destroy BehaviorSubscriber [this]
  (subscriber-destroy this))

(defmethod destroy BehaviorFilteredSubscriber [this]
  (subscriber-destroy this))

;; === Stream Factories ===

(defn create-stream
  ([] (create-stream nil))
  ([val] (let [subs (atom #{})
               state #?(:cljs    (reagent/atom val)
                        :default (atom val))]

           (add-watch state :state-watcher #(doseq [sub @subs] (sub %4)))

           (->Stream subs state))))

(defn create-behavior-stream
  ([] (create-behavior-stream nil))
  ([val]
   (let [subs (atom #{})
         state #?(:cljs    (reagent/atom val)
                  :default (atom val))]

     (add-watch state :state-watcher #(doseq [sub @subs] (sub %4)))

     (->BehaviorStream subs state))))

;; === Subscriber Factories ===

(derive Stream ::subscriber)

(derive BehaviorStream ::behavior-subscriber)

(derive Subscriber ::subscriber)

(derive BehaviorSubscriber ::behavior-subscriber)

(derive BehaviorFilteredSubscriber ::behavior-subscriber)

(defmulti create-subscriber (fn [stream _ _ _] (type stream)))

(defmethod create-subscriber ::subscriber [stream watcher-key subs state]
  (->Subscriber subs state {:state (:state stream) :watcher-key watcher-key}))

(defmethod create-subscriber ::behavior-subscriber [stream watcher-key subs state]
  (->BehaviorSubscriber subs state {:state (:state stream) :watcher-key watcher-key}))

(defmulti create-filtered-subscriber (fn [stream _ _ _ _] (type stream)))

(defmethod create-filtered-subscriber ::subscriber [stream _ watcher-key subs state]
  (create-subscriber stream watcher-key subs state))

(defmethod create-filtered-subscriber ::behavior-subscriber [stream filter watcher-key subs state]
  (->BehaviorFilteredSubscriber subs state {:state (:state stream) :watcher-key watcher-key} filter))

;; === Watcher Keys Generator ===

(defonce ^:private watcher-key
         (let [counter (atom 0)]
           #(swap! counter inc)))

;; === Operators ===

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
