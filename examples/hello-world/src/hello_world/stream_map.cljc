(ns hello-world.stream-map
  (:require [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

;; Basic usage of map operator

(def musicians (re-streamer/stream))
(def guitarists (re-streamer/map musicians :guitarists))

(subscribe musicians #(println (str "musicians: " %)))
(subscribe guitarists #(println (str "guitarists: " %)))

(emit musicians {:drummers   ["Ringo Starr" "Charlie Watts"]
                 :guitarists ["George Harrison" "Keith Richards"]})

;; output:
;; guitarists: ["George Harrison" "Keith Richards"]
;; musicians: {:drummers ["Ringo Starr" "Charlie Watts"], :guitarists ["George Harrison" "Keith Richards"]}
