(ns reagent-example.core
  (:require [clojure.string :refer [includes?]]
            [reagent.core :as r]
            [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

(def fruits ["apple" "pineapple" "orange" "blueberry"])
(def initial-state {:search "" :fruits []})
(def store (re-streamer/create-behavior-stream initial-state))
(def search (-> store
                (re-streamer/map :search)
                (re-streamer/distinct =)))

(defn filter-fruits [search fruits]
  (filter #(includes? % search) fruits))

(subscribe search #(emit store (assoc @(:state store) :fruits (filter-fruits % fruits))))

(defn update-search [search]
  (emit store (assoc @(:state store) :search search)))

(defn app []
  [:div
   [:h3 "Reactive Approach"]
   [:input {:on-change #(update-search (.. % -target -value))}]
   [:ul
    (for [fruit (:fruits @(:state store))]
      ^{:key fruit} [:li fruit])]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
