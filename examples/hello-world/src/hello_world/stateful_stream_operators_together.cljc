(ns hello-world.stateful-stream-operators-together
  (:require [re-streamer.stateful-stream :as re-streamer]))

;; Let's assume that you need to display some data with search
;; and you don't want to trigger the server
;; when search condition is empty or it's not changed

;; So first, define the initial state and pass it as initial value of stateful stream
(def init-state {:search "" :data ["Steve Ray Vaughan" "Chuck Berry" "Jimi Hendrix"]})
(def store (re-streamer/create init-state))

;; Define search subscriber using map, filter and distinct operators
(def search
  (-> store
      (re-streamer/map :search)
      (re-streamer/distinct =)
      (re-streamer/filter #(< 0 (count %)))))

;; Explanation:
;; First, extract search from the store's state
;; Then, use distinct operator to ensure that search subscriber
;; receives only different values
;; In the end, filter it to not get an empty string as search value

;; Subscribe to the search subscriber
((:subscribe! search) #(println (str "Fetch data from the server with condition: " %)))

;; output:
;; (there are no printed values, because search condition is an empty string)

;; Now, user enters new condition and press search button
((:emit! store) (assoc @(:state store) :search "Steve"))

;; output:
;; Fetch data from the server with condition: Steve

;; And press the button again
((:emit! store) (assoc @(:state store) :search "Steve"))

;; output:
;; (there are no printed values, because emitted value is the same as previous)

;; User deletes search condition and press the button
((:emit! store) (assoc @(:state store) :search ""))

;; output:
;; (there are no printed values, because search condition is an empty string)

;; And enters a new value
((:emit! store) (assoc @(:state store) :search "Hendrix"))

;; output:
;; Fetch data from the server with condition: Hendrix
