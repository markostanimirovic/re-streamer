(ns hello-world.stream-filter
  (:require [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

;; Basic usage of filter operator

(def numbers (re-streamer/stream 100))
(def positive-numbers (re-streamer/filter numbers #(< 0 %)))

(subscribe positive-numbers #(println %))

(emit numbers 0)

;; output:
;; (there are no printed values)

(emit numbers 10)

;; output:
;; 10
