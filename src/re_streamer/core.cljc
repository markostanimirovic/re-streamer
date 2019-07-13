(ns re-streamer.core
  #?(:cljs (:require [reagent.core :as reagent]))
  (:refer-clojure :rename {map c-map distinct c-distinct filter c-filter flush c-flush}))

(defprotocol Subscribable
  (subscribe [this sub])
  (unsubscribe [this sub])
  (destroy [this]))

(def default-subscribable-impl
  {:subscribe   (fn [this sub] (swap! (:subs this) conj sub))
   :unsubscribe (fn [this sub] (swap! (:subs this) disj sub))
   :destroy     (fn [this] (remove-watch (:state this) :watch))})

(def behavior-subscribable-impl
  (assoc default-subscribable-impl
    :subscribe (fn [this sub]
                 (swap! (:subs this) conj sub)
                 (sub @(:state this)))))

(defprotocol Emitable
  (emit [this val])
  (flush [this]))

(def default-emitable-impl
  {:emit  (fn [this val] (reset! (:state this) val))
   :flush (fn [this] (reset! (:subs this) #{}))})

(defrecord Subscriber [subs state])

(extend Subscriber
  Subscribable
  default-subscribable-impl)

(defrecord BehaviorSubscriber [subs state])

(extend BehaviorSubscriber
  Subscribable
  behavior-subscribable-impl)

(defrecord Stream [subs state])

(extend Stream
  Subscribable
  default-subscribable-impl
  Emitable
  default-emitable-impl)


(defrecord BehaviorStream [subs state])

(extend BehaviorStream
  Subscribable
  behavior-subscribable-impl
  Emitable
  default-emitable-impl)

(defn create-stream []
  (let [subs (atom #{})
        state #?(:cljs    (reagent/atom nil)
                 :default (atom nil))]

    (add-watch state :watch #(doseq [sub @subs] (sub %4)))

    (->Stream subs state)))

(defn create-behavior-stream
  ([] (create-behavior-stream nil))
  ([val]
   (let [subs (atom #{})
         state #?(:cljs    (reagent/atom val)
                  :default (atom val))]

     (add-watch state :watch #(doseq [sub @subs] (sub %4)))

     (->BehaviorStream subs state))))
