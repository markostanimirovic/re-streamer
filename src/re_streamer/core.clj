(ns re-streamer.core)

(defn stateful-stream
  ([] stateful-stream nil)
  ([val]
   (let [subs (atom #{})
         state (atom val)]
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
    {:subscribe! (fn [sub]
                   (swap! subs conj sub)
                   sub)
     :unsubscribe! (fn [sub]
                     (swap! subs disj sub)
                     nil)
     :emit!      (fn [val]
                   (doseq [sub @subs] (sub val)))}))
