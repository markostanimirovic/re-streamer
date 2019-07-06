(ns re-streamer.core
  #?(:cljs (:require [reagent.core :as reagent])))

(defn stream []
  (let [subs (atom #{})]
    {:subscribe!   (fn [sub]
                     (swap! subs conj sub)
                     sub)
     :unsubscribe! (fn [sub]
                     (swap! subs disj sub)
                     nil)
     :emit!        (fn [val]
                     (doseq [sub @subs] (sub val)))
     :flush!       (fn []
                     (reset! subs #{})
                     nil)}))

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
      :flush!       (fn []
                      (reset! subs #{})
                      (reset! state nil))
      :state        state})))

(defn subscriber [stream]
  (select-keys stream [:subscribe! :unsubscribe! :state]))

(defn combined-stateful-subscriber [streams]
  (let [subs (atom #{})
        states (map :state streams)]
    (doseq [state states]
      (add-watch state :watch (fn [_ _ _ _]
                                (doseq [sub @subs]
                                  (sub (map #(-> @%) states))))))
    {:subscribe!   (fn [sub]
                     (swap! subs conj sub)
                     (sub (map #(-> @%) states)))
     :unsubscribe! (fn [sub]
                     (swap! subs disj sub))
     :state        states}))

(defn emitter [stream]
  (select-keys stream [:emit! :flush!]))
