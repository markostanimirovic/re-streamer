(ns hello-world.stream-filter
  (:require [re-streamer.stream :as re-streamer]))

;; Basic usage of filter operator

(def numbers (re-streamer/create))
(def positive-numbers (re-streamer/filter numbers #(< 0 %)))

((:subscribe! positive-numbers) #(println %))

((:emit! numbers) 0)

;; output:
;; (there are no printed values)

((:emit! numbers) 10)

;; output:
;; 10
