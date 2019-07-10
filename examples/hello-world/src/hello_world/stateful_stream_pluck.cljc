(ns hello-world.stateful-stream-pluck
  (:require [re-streamer.stateful-stream :as re-streamer]))

;; Basic usage of pluck operator

(def panel (re-streamer/create {:head   "Pluck"
                                :body   "Operator from Re-Streamer Library"
                                :footer "2019 Marko Stanimirovic"}))
(def head-and-body (re-streamer/pluck panel [:head :body]))

(println @(:state panel))

;; output:
;; {:head Pluck, :body Operator from Re-Streamer Library, :footer 2019 Marko Stanimirovic}

(println @(:state head-and-body))

;; output:
;; {:head Pluck, :body Operator from Re-Streamer Library}

((:subscribe! head-and-body) #(println (str "head-and-body: " %)))

;; output:
;; head-and-body: {:head "Pluck", :body "Operator from Re-Streamer Library"}

((:emit! panel) (assoc @(:state panel) :body "Stateful Stream Operator"))

;; output:
;; head-and-body: {:head "Pluck", :body "Stateful Stream Operator"}
