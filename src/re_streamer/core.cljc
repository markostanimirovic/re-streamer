(ns re-streamer.core
  #?(:cljs (:require [reagent.core :as reagent]))
  (:refer-clojure :rename {map c-map distinct c-distinct filter c-filter flush c-flush}))

;; === types ===

(defprotocol Subscribable
  (subscribe [this sub])
  (unsubscribe [this sub])
  (destroy [this]))

(defprotocol Emitable
  (emit [this val])
  (flush [this]))

;; === Stream ===

(defrecord Stream [subs state])

(defonce ^:private stream-subscriber-impl
         {:subscribe   (fn [this sub]
                         (swap! (:subs this) conj sub)
                         sub)
          :unsubscribe (fn [this sub] (swap! (:subs this) disj sub))
          :destroy     (fn [this] (remove-watch (:state this) :state-watcher))})

(defonce ^:private emitter-impl
         {:emit  (fn [this val] (reset! (:state this) val))
          :flush (fn [this] (reset! (:subs this) #{}))})

(extend Stream
  Subscribable
  stream-subscriber-impl
  Emitable
  emitter-impl)

(derive Stream ::subscriber)

;; === BehaviorStream ===

(defrecord BehaviorStream [subs state])

(defonce ^:private behavior-stream-subscriber-impl
         (assoc stream-subscriber-impl
           :subscribe (fn [this sub]
                        (swap! (:subs this) conj sub)
                        (sub @(:state this))
                        sub)))

(extend BehaviorStream
  Subscribable
  behavior-stream-subscriber-impl
  Emitable
  emitter-impl)

(derive BehaviorStream ::behavior-subscriber)

;; === Subscriber ===

(defrecord Subscriber [subs state parent])

(defonce ^:private subscriber-impl
         (assoc stream-subscriber-impl
           :destroy (fn [this]
                      (remove-watch (:state this) :state-watcher)
                      (remove-watch (:state (:parent this)) (:watcher-key (:parent this))))))

(extend Subscriber
  Subscribable
  subscriber-impl)

(derive Subscriber ::subscriber)

;; === BehaviorSubscriber ===

(defrecord BehaviorSubscriber [subs state parent])

(defonce ^:private behavior-subscriber-impl
         (assoc behavior-stream-subscriber-impl
           :destroy (fn [this]
                      (remove-watch (:state this) :state-watcher)
                      (remove-watch (:state (:parent this)) (:watcher-key (:parent this))))))

(extend BehaviorSubscriber
  Subscribable
  behavior-subscriber-impl)

(derive BehaviorSubscriber ::behavior-subscriber)

;; === BehaviorFilteredSubscriber ===

(defrecord BehaviorFilteredSubscriber [subs state parent filter])

(defonce ^:private behavior-filtered-subscriber-impl
         (assoc behavior-subscriber-impl
           :subscribe (fn [this sub]
                        (swap! (:subs this) conj sub)
                        (if ((:filter this) @(:state (:parent this)))
                          (sub @(:state this)))
                        sub)))

(extend BehaviorFilteredSubscriber
  Subscribable
  behavior-filtered-subscriber-impl)

(derive BehaviorFilteredSubscriber ::behavior-subscriber)

;; === factories ===

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
