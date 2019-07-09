(ns hello-world.stream-operators
  (:require [re-streamer.stream :as stream]))

;; example 1: map operator

(def bar (stream/create))
(def mapped-bar (stream/map bar inc))

(def mapped-bar-sub ((:subscribe! mapped-bar) #(println %)))

((:emit! bar) 10)

;; output:
;; 11

((:unsubscribe! mapped-bar) mapped-bar-sub)

((:emit! bar) 100)

;; output:
;; (there are no printed values)

;; example 2: map operator

(def bar (stream/create))
(def mapped-bar
  (-> bar
      (stream/map :count)
      (stream/map inc)))

((:subscribe! mapped-bar) #(println %))

((:emit! bar) {:count 1 :items ["foo" "bar"]})

;; output:
;; 2

;; example 3: filter operator

(def bar (stream/create))
(def filtered-bar (stream/filter bar #(> % 10)))

((:subscribe! filtered-bar) #(println %))

((:emit! bar) 10)

;; output:
;; (there are no printed values)

((:emit! bar) 11)

;; output:
;; 11

;; example 4: map and filter operators

(def bar (stream/create))
(def baz
  (-> bar
      (stream/map :count)
      (stream/filter #(> % 10))))

((:subscribe! baz) #(println %))

((:emit! bar) {:count 100})

;; output:
;; 100

((:emit! bar) {:count 1})

;; output:
;; (there are no printed values)
