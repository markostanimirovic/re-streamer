(ns hello-world.stream-map
  (:require [re-streamer.stream :as re-streamer]))

;; Basic usage of map operator

(def musicians (re-streamer/create))
(def guitarists (re-streamer/map musicians :guitarists))

((:subscribe! musicians) #(println (str "musicians: " %)))
((:subscribe! guitarists) #(println (str "guitarists: " %)))

((:emit! musicians) {:drummers   ["Ringo Starr" "Charlie Watts"]
                     :guitarists ["George Harrison" "Keith Richards"]})

;; output:
;; guitarists: ["George Harrison" "Keith Richards"]
;; musicians: {:drummers ["Ringo Starr" "Charlie Watts"], :guitarists ["George Harrison" "Keith Richards"]}
