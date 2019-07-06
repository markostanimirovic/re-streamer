(ns hello-world.stream
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

;; to remove all subscriptions and set state value to nil use flush function
(def sub3 ((:subscribe! bar) #(println (str (:message %) " Really"))))

(def sub4 ((:subscribe! bar) #(println (str (:message %) " in Clojure(Script)"))))

((:emit! bar) {:message "Re-Streamer Rocks"})

;; output:
;; Re-Streamer Rocks Really (sub3)
;; Re-Streamer Rocks in Clojure(Script) (sub4)

;; let's now flush the stream
((:flush! bar))

((:emit! bar) {:message "Reactive Programming"})

;; output:
;; (there are no printed values, because there are no subscriptions)

;; so, if you want to remove all subscriptions,
;; use flush instead of calling unsubscribe many times

;; first way
((:unsubscribe! bar) sub3)
((:unsubscribe! bar) sub4)

;; second way
((:flush! bar))
