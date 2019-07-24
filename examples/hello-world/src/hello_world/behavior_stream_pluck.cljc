(ns hello-world.behavior-stream-pluck
  (:require [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

;; Basic usage of pluck operator

(def panel (re-streamer/behavior-stream {:head   "Pluck"
                                         :body   "Operator from Re-Streamer Library"
                                         :footer "2019 Marko Stanimirovic"}))
(def head-and-body (re-streamer/pluck panel [:head :body]))

(println @(:state panel))

;; output:
;; {:head Pluck, :body Operator from Re-Streamer Library, :footer 2019 Marko Stanimirovic}

(println @(:state head-and-body))

;; output:
;; {:head Pluck, :body Operator from Re-Streamer Library}

(subscribe head-and-body #(println (str "head-and-body: " %)))

;; output:
;; head-and-body: {:head "Pluck", :body "Operator from Re-Streamer Library"}

(emit panel (assoc @(:state panel) :body "Behavior Stream Operator"))

;; output:
;; head-and-body: {:head "Pluck", :body "Behavior Stream Operator"}
