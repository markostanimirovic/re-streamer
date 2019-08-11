(ns hello-world.behavior-stream-skip
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]))

;; Basic usage of skip operator

(def counter (re-streamer/behavior-stream 0))
(def skipped-counter (re-streamer/skip counter 2))

(subscribe counter #(println (str "Counter subscription: " %)))

;; output:
;; Counter subscription: 0

(subscribe skipped-counter #(println (str "Skipped counter subscription: " %)))

;; output:
;; (there are no printed values, because first two emits are skipped)

(emit counter 1)

;; output:
;; Counter subscription: 1

(emit counter 2)

;; output:
;; Counter subscription: 2
;; Skipped counter subscription: 2

(emit counter 3)

;; output:
;; Counter subscription: 3
;; Skipped counter subscription: 3
