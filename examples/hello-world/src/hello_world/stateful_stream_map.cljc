(ns hello-world.stateful-stream-map
  (:require [re-streamer.stateful-stream :as re-streamer]))

;; Basic usage of map operator

(def post (re-streamer/create {:title "Re-Streamer" :message "Reactive Programming Library"}))
(def title (re-streamer/map post :title))
(def message (re-streamer/map post :message))

(println @(:state post))

;; output:
;; {:title Re-Streamer, :message Reactive Programming Library}

(println @(:state title))

;; output:
;; Re-Streamer

(println @(:state message))

;; output:
;; Reactive Programming Library

((:subscribe! post) #(println (str "post: " %)))

;; output:
;; post: {:title "Re-Streamer", :message "Reactive Programming Library"}

((:subscribe! title) #(println (str "title: " %)))

;; output:
;; title: Re-Streamer

((:emit! post) {:title "Clojure" :message "Functional Programming Language"})

;; output:
;; title: Clojure
;; post: {:title "Clojure", :message "Functional Programming Language"}

((:subscribe! message) #(println (str "message: " %)))

;; output:
;; message: Functional Programming Language

((:emit! post) {:title "Java" :message "Object Oriented Programming Language"})

;; output:
;; title: Java
;; message: Object Oriented Programming Language
;; post: {:title "Java", :message "Object Oriented Programming Language"}
