(ns hello-world.stateful-stream-distinct
  (:require [re-streamer.stateful-stream :as re-streamer]))

;; Basic usage of distinct operator

(def login-form (re-streamer/create {:username "stanimirovic" :password "secret"}))
(def login-action (re-streamer/distinct login-form =))

((:subscribe! login-action) #(println (str "Logging user with credentials: " %)))

;; output:
;; Logging user with credentials: {:username "stanimirovic", :password "secret"}

((:emit! login-form) {:username "stanimirovic" :password "secret"})

;; output:
;; (there are no printed values)

((:emit! login-form) {:username "stanimirovic" :password "secret123"})

;; output:
;; Logging user with credentials: {:username "stanimirovic", :password "secret123"}
