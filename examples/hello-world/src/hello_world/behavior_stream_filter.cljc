(ns hello-world.behavior-stream-filter
  (:require [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

;; Basic usage of filter operator

(def numbers (re-streamer/behavior-stream 0))
(def even-numbers (re-streamer/filter numbers even?))

(subscribe even-numbers #(println (str "Emitted: " %)))

;; output:
;; Emitted: 0

(emit numbers 1)

;; output:
;; (there are no printed values)

(emit numbers 2)

;; output:
;; Emitted: 2
