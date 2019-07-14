(ns hello-world.behavior-stream-distinct
  (:require [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

;; Basic usage of distinct operator

(def login-form (re-streamer/create-behavior-stream {:username "stanimirovic" :password "secret"}))
(def login-action (re-streamer/distinct login-form =))

(subscribe login-action #(println (str "Logging user with credentials: " %)))

;; output:
;; Logging user with credentials: {:username "stanimirovic", :password "secret"}

(emit login-form {:username "stanimirovic" :password "secret"})

;; output:
;; (there are no printed values)

(emit login-form {:username "stanimirovic" :password "secret123"})

;; output:
;; Logging user with credentials: {:username "stanimirovic", :password "secret123"}
