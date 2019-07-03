(ns re-streamer.core)

(defn- emit!
  [state val subs]
  (do (reset! state val)
      (doseq [sub @subs] (sub @state))))

(defn stateful-stream [val]
  (let [subs (atom [])
        state (atom val)]
    {:subscribe! (fn [sub]
                   (do (swap! subs conj sub)
                       (sub @state)))
     :emit!      (fn [val]
                   (emit! state val subs))}))

(defn stream []
  (let [subs (atom [])
        state (atom nil)]
    {:subscribe! (fn [sub]
                   (swap! subs conj sub))
     :emit!      (fn [val]
                   (emit! state val subs))}))
