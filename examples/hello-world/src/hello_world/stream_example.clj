(ns hello-world.stream-example
  (:require [re-streamer.core :as re-streamer]))

;; stream

;; create stream
(def bar (re-streamer/stream))

;; subscribe to the stream
(def sub1 ((:subscribe! bar) #(println (:message %))))

;; emit new value
((:emit! bar) {:message "World"})

;; output:
;; World

;; emit new value
((:emit! bar) {:message "Developers"})

;; output:
;; Developers

;; add one more subscription
(def sub2 ((:subscribe! bar) #(println (str "Hello " (:message %)))))

;; emit new value
((:emit! bar) {:message "Clojure Developers"})

;; output:
;; Clojure Developers (first subscription)
;; Hello Clojure Developers (second subscription)

; remove second subscription
((:unsubscribe! bar) sub2)

;; emit new value
((:emit! bar) {:message "Functional Programming"})

;; output:
;; Functional Programming (first subscription)

;; in the end, remove first subscription in order to reduce memory leaks
((:unsubscribe! bar) sub1)
