(ns hello-world.stream-operators-together
  (:require [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

;; Let's assume that you want to implement Router for REST API

;; First, you need to create a router
(def router (re-streamer/stream))

;; Define musician controller
(def musician-ctrl (-> router
                       (re-streamer/filter #(= (:route %) "/musicians"))
                       (re-streamer/pluck [:method :data])))

;; Define insert musician handler
(def insert-musician (-> musician-ctrl
                         (re-streamer/filter #(= (:method %) "POST"))
                         (re-streamer/map :data)))

;; Define get musicians handler
(def get-musicians (re-streamer/filter musician-ctrl #(= (:method %) "GET")))

;; Subscribe to the handlers
(subscribe insert-musician #(println (str "Insert musician: " %)))
(subscribe get-musicians (fn [_] (println "Fetch musicians from database...")))

;; There is new POST request with /musicians route
(emit router {:route  "/musicians"
              :method "POST"
              :data   {:name "Mick Jagger" :band "Rolling Stones"}})

;; output:
;; Insert musician: {:name "Mick Jagger", :band "Rolling Stones"}

;; There is new GET request with /musicians route
(emit router {:route "/musicians" :method "GET"})

;; output:
;; Fetch musicians from database...
