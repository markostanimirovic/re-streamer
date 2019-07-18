(ns reagent-example.core
  (:require [clojure.string :refer [includes?]]
            [reagent.core :as r]
            [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

(defn fruits-resource []
  (let [data ["apple" "pineapple" "orange" "blueberry" "cherry"]]
    {:fetch-fruits (fn [search]
                     (filter #(includes? % search) data))}))

(defn fruits-facade-factory []
  (let [initial-state {:search "" :fruits []}
        store (re-streamer/create-behavior-stream initial-state)

        fruits (re-streamer/map store :fruits)
        search (-> store
                   (re-streamer/map :search)
                   (re-streamer/distinct =))

        resource (fruits-resource)
        fetch-fruits (:fetch-fruits resource)]

    (subscribe search #(emit store (assoc @(:state store)
                                     :fruits (fetch-fruits %))))

    {:fruits        (:state fruits)
     :update-search (fn [e]
                      (emit store (assoc @(:state store)
                                    :search (.. e -target -value))))}))

(defonce fruits-facade (fruits-facade-factory))

(defn fruits-component []
  (let [facade fruits-facade
        fruits (:fruits facade)
        update-search (:update-search facade)]
    [:div
     [:h3 "Fruits"]
     [:input {:on-change #(update-search %)}]
     [:ul (for [fruit @fruits]
            ^{:key fruit} [:li fruit])]]))

(defn mount-root []
  (r/render [fruits-component] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
