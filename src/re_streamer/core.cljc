(ns re-streamer.core
  #?(:cljs (:require [reagent.core :as reagent])))

(defn stateful-stream
  ([] (stateful-stream nil))
  ([val]
   (let [subs (atom #{})
         state #?(:cljs    (reagent/atom val)
                  :default (atom val))]
     {:subscribe!   (fn [sub]
                      (swap! subs conj sub)
                      (sub @state)
                      sub)
      :unsubscribe! (fn [sub]
                      (swap! subs disj sub)
                      nil)
      :emit!        (fn [val]
                      (reset! state val)
                      (doseq [sub @subs] (sub @state)))
      :state        state})))

(defn stream []
  (let [subs (atom #{})]
    {:subscribe!   (fn [sub]
                     (swap! subs conj sub)
                     sub)
     :unsubscribe! (fn [sub]
                     (swap! subs disj sub)
                     nil)
     :emit!        (fn [val]
                     (doseq [sub @subs] (sub val)))}))

(defn subscriber [stream]
  (select-keys stream [:subscribe! :unsubscribe! :state]))

(defn emitter [stream]
  (select-keys stream [:emit!]))
