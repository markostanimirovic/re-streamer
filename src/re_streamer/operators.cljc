(ns re-streamer.operators
  (:require [re-streamer.core :as core]
            #?(:cljs [reagent.core :as reagent]))
  (:refer-clojure :rename {map c-map filter c-filter}))

;; stream/subscriber operators

(defn map [stream f]
  (core/subscriber (assoc stream :subscribe! (fn [sub]
                                               ((:subscribe! stream) (comp sub f))))))

(defn filter [stream f]
  (core/subscriber (assoc stream :subscribe! (fn [sub]
                                               ((:subscribe! stream) #(if (f %) (sub %)))))))

;; stateful-stream/subscriber operators

(defn stateful-map [stream f]
  (let [state #?(:cljs    (reagent/atom (f @(:state stream)))
                 :default (atom (f @(:state stream))))]
    (add-watch (:state stream) :watch #(reset! state (f %4)))
    (core/subscriber (assoc stream :subscribe! (fn [sub]
                                                 ((:subscribe! stream) (comp sub f)))
                                   :state state))))

(defn stateful-filter [stream f]
  (let [state #?(:cljs    (reagent/atom (if (f @(:state stream)) @(:state stream)))
                 :default (atom (if (f @(:state stream)) @(:state stream))))]
    (add-watch (:state stream) :watch #(if (f %4) (reset! state %4)))
    (core/subscriber (assoc stream :subscribe! (fn [sub]
                                                 ((:subscribe! stream) #(if (f %) (sub %))))
                                   :state state))))