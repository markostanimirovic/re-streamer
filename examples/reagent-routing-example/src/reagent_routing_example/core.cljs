(ns reagent-routing-example.core
  (:require
    [reagent.core :as r]
    [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

(def current-route (.. js/window -location -hash))

(println current-route)

(def router (re-streamer/create-behavior-stream "#home"))

(subscribe router #(set! (.. js/window -location -hash) %))

(defn navigate [route]
  (emit router route))

(defn home-page []
  [:div [:h2 "Welcome to Home Page"]
   [:button {:on-click #(navigate "#home")} "Home"]
   [:button {:on-click #(navigate "#about")} "About"]])

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
