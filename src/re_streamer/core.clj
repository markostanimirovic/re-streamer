(ns re-streamer.core)

(defn state-stream [initial-value]
  (let [callbacks (atom [])
        value (atom initial-value)]
    {:subscribe! (fn [callback]
                   (do (swap! callbacks conj callback)
                       (callback @value)))
     :emit!      (fn [new-value]
                   (do (reset! value new-value)
                       (doseq [callback @callbacks] (callback @value))))}))

(defn stream []
  (let [callbacks (atom [])
        value (atom nil)]
    {:subscribe! (fn [callback]
                   (do (swap! callbacks conj callback)))
     :emit!      (fn [new-value]
                   (do (reset! value new-value)
                       (doseq [callback @callbacks] (callback @value))))}))
