(ns hello-world.stateful-stream-operators-advanced
  (:require [re-streamer.stateful-stream :as stateful-stream]))

;; example 1: map and distinct

(def foo (stateful-stream/create {:bar {:baz 1} :message "Message"}))
(def mapped-foo1 (stateful-stream/map (stateful-stream/map foo :bar :mapped-foo1)
                                      (comp inc :baz)
                                      :mapped-foo1))
(def mapped-foo2 (stateful-stream/distinct (stateful-stream/map foo :message :mapped-foo2) = :mapped-foo2))

;; or using thread first macro:
(def mapped-foo1
  (-> foo
      (stateful-stream/map :bar :mapped-foo1)
      (stateful-stream/map (comp inc :baz) :mapped-foo1)))

(def mapped-foo2
  (-> foo
      (stateful-stream/map :message :mapped-foo2)
      (stateful-stream/distinct = :mapped-foo2)))

((:subscribe! mapped-foo1) #(println (str "foo1: " %)))

;; output:
;; foo1: 2

((:subscribe! mapped-foo2) #(println (str "foo2: " %)))

;; output:
;; foo2: Message

((:emit! foo) {:bar {:baz 10} :message "Message10"})

;; output:
;; foo2: Message10
;; foo1: 11

((:emit! foo) {:bar {:baz 10} :message "Message10"})

;; output:
;; foo1: 11

((:emit! foo) {:bar {:baz 10} :message "Message100"})

;; output:
;; foo2: Message100
;; foo1: 11

;; example 2: pluck and distinct

(def initial-state {:search     ""
                    :pagination {:items-per-page 10 :current-page 1}
                    :data       ["Mick Jagger" "Keith Richards" "Eric Clapton"]})

(def store (stateful-stream/create initial-state))

(def get-data-params
  (stateful-stream/distinct
    (stateful-stream/pluck store [:search :pagination] :get-data-params) = :get-data-params))

;; or using thread first macro:
(def get-data-params
  (-> store
      (stateful-stream/pluck [:search :pagination] :get-data-params)
      (stateful-stream/distinct = :get-data-params)))

((:subscribe! get-data-params) #(println (str "Fetch new data from the resource with params: " %)))

;; output:
;; Fetch new data from the resource with params: {:search "", :pagination {:items-per-page 10, :current-page 1}}

((:emit! store) {:search     "Mick"
                 :pagination {:items-per-page 10 :current-page 2}
                 :data       ["Mick Jagger" "Keith Richards" "Eric Clapton"]})

;; output:
;; Fetch new data from the resource with params: {:search "Mick", :pagination {:items-per-page 10, :current-page 2}}

((:emit! store) {:search     "Mick"
                 :pagination {:items-per-page 10 :current-page 2}
                 :data       ["Mick Jagger" "Keith Richards" "Eric Clapton"]})

;; output:
;; (there are no printed values)

((:emit! store) {:search     "Keith"
                 :pagination {:items-per-page 10 :current-page 2}
                 :data       ["Mick Jagger" "Keith Richards" "Eric Clapton"]})

;; output:
;; Fetch new data from the resource with params: {:search "Keith", :pagination {:items-per-page 10, :current-page 2}}