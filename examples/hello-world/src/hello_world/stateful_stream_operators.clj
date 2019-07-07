(ns hello-world.stateful-stream-operators
  (:require [re-streamer.stateful-stream :as stateful-stream]))

;; example 1: map

(def foo (stateful-stream/create {:title "Re-Streamer" :message "Hello World"}))
(def mapped-foo (stateful-stream/map foo :message :mapped-foo))

(println @(:state foo))

;; output:
;; {:title Re-Streamer, :message Hello World}

(println @(:state mapped-foo))

;; output:
;; Hello World

((:subscribe! mapped-foo) #(println %))

;; output:
;; Hello World

((:emit! foo) {:title "Clojure" :message "Hello Clojure"})

;; output:
;; Hello Clojure

;; example 2: pluck

(def foo (stateful-stream/create {:title "Re-Streamer" :message "Hello World" :language "Clojure"}))
(def plucked-foo (stateful-stream/pluck foo [:title :message] :plucked-foo))

(println @(:state foo))

;; output:
;; {:title Re-Streamer, :message Hello World, :language Clojure}

(println @(:state plucked-foo))

;; output:
;; {:title Re-Streamer, :message Hello World}

((:subscribe! plucked-foo) #(println %))

;; output:
;; {:title Re-Streamer, :message Hello World}

((:emit! foo) {:title "Java" :message "Hello Java" :language "Java"})

;; output:
;; {:title Java, :message Hello Java}

;; example 3: distinct

(def foo (stateful-stream/create {:title "Re-Streamer" :message "Hello World" :language "Clojure"}))
(def distinct-foo (stateful-stream/distinct foo = :distinct-foo))

((:subscribe! distinct-foo) #(println %))

;; output:
;; {:title Re-Streamer, :message Hello World, :language Clojure}

((:emit! foo) {:title "Java" :message "Hello Java" :language "Java"})

;; output:
;; {:title Java, :message Hello Java, :language Java}

((:emit! foo) {:title "Java" :message "Hello Java" :language "Java"})

;; output:
;; (there are no printed values)
