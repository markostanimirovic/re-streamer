(ns hello-world.stream
  (:require [re-streamer.stream :as stream]))

;; Create stream
(def bar (stream/create))

;; Subscribe to the stream
(def sub1 ((:subscribe! bar) #(println (:message %))))

;; Emit new value
((:emit! bar) {:message "World"})

;; output:
;; World

;; Emit new value
((:emit! bar) {:message "Developers"})

;; output:
;; Developers

;; Add one more subscription
(def sub2 ((:subscribe! bar) #(println (str "Hello " (:message %)))))

;; Emit new value
((:emit! bar) {:message "Clojure Developers"})

;; output:
;; Clojure Developers (first subscription)
;; Hello Clojure Developers (second subscription)

;; Remove second subscription
((:unsubscribe! bar) sub2)

;; Emit new value
((:emit! bar) {:message "Functional Programming"})

;; output:
;; Functional Programming (first subscription)

;; Remove first subscription in order to reduce memory leaks
((:unsubscribe! bar) sub1)

(def sub3 ((:subscribe! bar) #(println (str (:message %) " Really"))))

(def sub4 ((:subscribe! bar) #(println (str (:message %) " in Clojure(Script)"))))

((:emit! bar) {:message "Re-Streamer Rocks"})

;; output:
;; Re-Streamer Rocks Really (sub3)
;; Re-Streamer Rocks in Clojure(Script) (sub4)

;; To remove all subscriptions use flush function

;; Let's now flush the stream
((:flush! bar))

((:emit! bar) {:message "Reactive Programming"})

;; output:
;; (there are no printed values, because there are no subscriptions)

;; So, if you want to remove all subscriptions,
;; use flush instead of calling unsubscribe many times

;; First way
((:unsubscribe! bar) sub3)
((:unsubscribe! bar) sub4)

;; Second way
((:flush! bar))
