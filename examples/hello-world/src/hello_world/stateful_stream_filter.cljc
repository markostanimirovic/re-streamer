(ns hello-world.stateful-stream-filter
  (:require [re-streamer.stateful-stream :as re-streamer]))

;; Basic usage of filter operator

(def numbers (re-streamer/create 0))
(def even-numbers (re-streamer/filter numbers even?))

((:subscribe! even-numbers) #(println (str "Emitted: " %)))

;; output:
;; Emitted: 0

((:emit! numbers) 1)

;; output:
;; (there are no printed values)

((:emit! numbers) 2)

;; output:
;; Emitted: 2
