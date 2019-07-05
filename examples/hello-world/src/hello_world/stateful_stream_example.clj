(ns hello-world.stateful-stream-example
  (:require [re-streamer.core :as re-streamer]))

;; stateful-stream

;; create stateful stream with initial value
(def foo (re-streamer/stateful-stream 10))

;; subscribe to the stateful stream
(def sub1 ((:subscribe! foo) #(println %)))

;; output:
;; 10

;; emit new value
((:emit! foo) 100)

;; output:
;; 100

;; add one more subscription
(def sub2 ((:subscribe! foo) #(println (inc %))))

;; output:
;; 101

;; emit new value
((:emit! foo) 1000)

;; output:
;; 1000 (first subscription)
;; 1001 (second subscription)

;; remove first subscription
((:unsubscribe! foo) sub1)

;; emit new value
((:emit! foo) 10000)

;; output:
;; 10001 (second subscription)

;; also, you can get a state from stateful stream in any moment
(println @(:state foo))

;; output:
;; 10000

;; in the end, remove second subscription in order to reduce memory leaks
((:unsubscribe! foo) sub2)
