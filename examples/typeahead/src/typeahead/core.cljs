(ns typeahead.core
  (:require [clojure.string :refer [includes?]]
            [reagent.core :as r]
            [re-streamer.core :as re-streamer :refer [subscribe emit]]))

(defn fruits-resource []
  (let [data ["apple" "pineapple" "orange" "blueberry" "cherry"]]
    {:get-fruits (fn [search]
                   (filter #(includes? % search) data))}))

(defn fruits-facade []
  (let [initial-state {:search "" :fruits []}
        store (re-streamer/behavior-stream initial-state)
        fruits (re-streamer/map store :fruits)
        search (-> store
                   (re-streamer/map :search)
                   (re-streamer/distinct =))
        resource (fruits-resource)]

    (subscribe search #(emit store (assoc @(:state store) :fruits ((:get-fruits resource) %))))

    {:fruits        (:state fruits)
     :update-search #(emit store (assoc @(:state store) :search %))}))

(defn fruits-container []
  (let [facade (fruits-facade)
        fruits (:fruits facade)
        update-search (:update-search facade)]
    (fn []
      [:div
       [:h3 "Search Fruits"]
       [:input {:on-change #(update-search (.. % -target -value))}]
       [:ul (for [fruit @fruits]
              ^{:key fruit} [:li fruit])]])))

(defn mount-root []
  (r/render [fruits-container] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
