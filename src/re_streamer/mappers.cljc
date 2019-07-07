(ns re-streamer.mappers)

(defn ->emitter [stream]
  (select-keys stream [:emit! :flush!]))

(defn ->subscriber [stream]
  (select-keys stream [:subscribe! :unsubscribe! :state]))