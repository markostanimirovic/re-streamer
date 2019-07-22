(ns reagent-routing-example.core
  (:require
    [reagent.core :as r]
    [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

;; === Pages ===

(defn home-page []
  [:div
   [:h3 "Home"]
   [:p "This is home page"]])

(defn about-page []
  [:div
   [:h3 "About"]
   [:p "Thi is about page"]])

;; === Router with helper functions ===

(defmulti current-page #(-> %))

(defmethod current-page "#home" [_]
  home-page)
(defmethod current-page "#about" [_]
  about-page)
(defmethod current-page :default [_]
  home-page)

(def init-route {:route (.. js/window -location -hash)
                 :page  (current-page (.. js/window -location -hash))})
(def router (re-streamer/create-behavior-stream init-route))
(def router-outlet (re-streamer/map router :page))

(subscribe router #(set! (.. js/window -location -hash) (:route %)))

(defn navigate [route]
  (emit router {:route route
                :page  (current-page route)}))

;; === App Container ===

(defn app []
  [:div
   [:h2 "App Header"]
   [:button {:on-click #(navigate "#home")} "Home"]
   [:button {:on-click #(navigate "#about")} "About"]
   [:br]
   [@(:state router-outlet)]
   [:small "App Footer"]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
