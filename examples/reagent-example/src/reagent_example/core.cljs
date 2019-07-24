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
        store (re-streamer/behavior-stream initial-state)
        fruits (re-streamer/map store :fruits)
        search (-> store
                   (re-streamer/map :search)
                   (re-streamer/distinct =))
        resource (fruits-resource)
        fetch-fruits (:fetch-fruits resource)
        search-sub (subscribe search #(emit store (assoc @(:state store)
                                                    :fruits (fetch-fruits %))))]

    {:fruits        (:state fruits)
     :update-search (fn [e]
                      (emit store (assoc @(:state store)
                                    :search (.. e -target -value))))
     :destroy       #(unsubscribe search search-sub)}))

(defonce fruits-facade (fruits-facade-factory))

;; presentational components

(defn fruits-header [update-search]
  [:div
   [:h3 "Search Fruits"]
   [:input {:on-change #(update-search %)}]])

(defn fruits-list [fruits]
  [:ul (for [fruit @fruits]
         ^{:key fruit} [:li fruit])])

;; container component

(defn fruits-container []
  (let [facade fruits-facade
        fruits (:fruits facade)
        update-search (:update-search facade)
        destroy (:destroy facade)]
    (r/create-class {:reagent-render         (fn []
                                               [:div
                                                [fruits-header update-search]
                                                [fruits-list fruits]])

                     :component-will-unmount #(destroy)})))

(defn mount-root []
  (r/render [fruits-container] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
