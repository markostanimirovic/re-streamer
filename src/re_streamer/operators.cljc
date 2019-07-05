(ns re-streamer.operators
  (:require [re-streamer.core :as core])
  (:refer-clojure :rename {map c-map}))

(defn map [stream f]
  (let [state (:state stream)
        subs (:subs stream)]
    (assoc stream :subscribe! (fn [sub]
                                (swap! subs conj (comp sub f))
                                ((comp sub f) @state)
                                sub))))
