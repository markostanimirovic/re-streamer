(ns hello-world.stream-skip
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]))

;; Basic usage of skip operator

(def counter (re-streamer/stream 0))
(def skipped-counter (re-streamer/skip counter 2))

(subscribe counter #(println (str "Counter subscription: " %)))
(subscribe skipped-counter #(println (str "Skipped counter subscription: " %)))

(emit counter 1)

;; output:
;; Counter subscription: 1

(emit counter 2)

;; output:
;; Counter subscription: 2

(emit counter 3)

;; output:
;; Counter subscription: 3
;; Skipped counter subscription: 3
