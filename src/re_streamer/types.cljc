(ns re-streamer.types
  (:refer-clojure :rename {flush c-flush}))

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
         {:subscribe   (fn [this sub] (swap! (:subs this) conj sub))
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
                        (sub @(:state this)))))

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
                          (sub @(:state this))))))

(extend BehaviorFilteredSubscriber
  Subscribable
  behavior-filtered-subscriber-impl)

(derive BehaviorFilteredSubscriber ::behavior-subscriber)
