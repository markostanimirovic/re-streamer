(ns hello-world.stateful-stream
  (:require [re-streamer.stateful-stream :as stateful-stream]))

;; Create stateful stream with initial value
;; If you don't pass it, initial value will be nil
(def foo (stateful-stream/create 10))

;; Subscribe to the stateful stream
(def sub1 ((:subscribe! foo) #(println %)))

;; output:
;; 10

;; Emit new value
((:emit! foo) 100)

;; output:
;; 100

;; Add one more subscription
(def sub2 ((:subscribe! foo) #(println (inc %))))

;; output:
;; 101

;; Emit new value
((:emit! foo) 1000)

;; output:
;; 1000 (first subscription)
;; 1001 (second subscription)

;; Remove first subscription
((:unsubscribe! foo) sub1)

;; Emit new value
((:emit! foo) 10000)

;; output:
;; 10001 (second subscription)

;; Also, you can get a state from stateful stream in any moment
(println @(:state foo))

;; output:
;; 10000

;; Remove second subscription in order to reduce memory leaks
((:unsubscribe! foo) sub2)

(def sub3 ((:subscribe! foo) #(println (+ 10 %))))

;; output:
;; 10010 (note: state is still alive)

(def sub4 ((:subscribe! foo) #(println (+ 100 %))))

;; output:
;; 10100

((:emit! foo) 20)

;; output:
;; 30 (sub3)
;; 120 (sub4)

;; To remove all subscriptions use flush function

;; Let's now flush the stream
((:flush! foo))

;; Check subscriptions
((:emit! foo) 10)

;; output:
;; (there are no printed values, because there are no subscriptions)

;; So, if you want to remove all subscriptions,
;; use flush instead of calling unsubscribe function many times

;; First way
((:unsubscribe! foo) sub3)
((:unsubscribe! foo) sub4)

;; Second way
((:flush! foo))

;; Stream is still alive
;; To destroy it, use destroy function
((:destroy! foo))