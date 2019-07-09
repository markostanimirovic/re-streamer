(ns re-streamer.stream
  (:refer-clojure :rename {map c-map filter c-filter}))

(defn create []
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

;; operators

(defn map [stream f]
  (-> (assoc stream :subscribe! (fn [sub]
                                  ((:subscribe! stream) (comp sub f))))
      (select-keys [:subscribe! :unsubscribe! :flush!])))

(defn filter [stream f]
  (-> (assoc stream :subscribe! (fn [sub]
                                  ((:subscribe! stream) #(if (f %) (sub %)))))
      (select-keys [:subscribe! :unsubscribe! :flush!])))
