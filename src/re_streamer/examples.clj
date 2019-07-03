(ns re-streamer.examples
  (:require [re-streamer.core :as re-streamer]))

;; state-stream

;; create state stream with initial value
(def foo (re-streamer/state-stream 10))

;; subscribe to the state stream
((:subscribe! foo) #(println %))

;; console-output:
;; 10

;; emit new value
((:emit! foo) 100)

;; console-output:
;; 100

;; add one more subscription
((:subscribe! foo) #(println (inc %)))

;; console-output:
;; 101

;; emit new value
((:emit! foo) 1000)

;; console-output:
;; 1000 (first subscription)
;; 1001 (second subscription)

;; stream

;; create stream
(def bar (re-streamer/stream))

;; subscribe to the stream
((:subscribe! bar) #(println (:message %)))

;; emit new value
((:emit! bar) {:message "World"})

;; console-output:
;; World

;; emit new value
((:emit! bar) {:message "Developers"})

;; console-output:
;; Developers

;; add one more subscription
((:subscribe! bar) #(println (str "Hello " (:message %))))

;; emit new value
((:emit! bar) {:message "Clojure Developers"})

;; console-output:
;; Clojure Developers (first subscription)
;; Hello Clojure Developers (second subscription)