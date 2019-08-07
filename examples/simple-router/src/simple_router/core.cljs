(ns simple-router.core
  (:require
    [reagent.core :as r]
    [re-streamer.core :as re-streamer :refer [subscribe emit]]))

;; === Pages ===

(defn home-page []
  [:div
   [:h3 "Home"]
   [:p "This is home page"]])

(defn about-page []
  [:div
   [:h3 "About"]
   [:p "This is about page"]])

(defn not-found-page []
  [:div
   [:h3 "404 Not Found"]
   [:p "Oops! Something went wrong!"]])

;; === Router with helper functions ===

(defmulti current-page #(-> %))

(defmethod current-page "#/home" [_]
  home-page)
(defmethod current-page "#/about" [_]
  about-page)
(defmethod current-page :default [_]
  not-found-page)

(def init-route {:route (.. js/window -location -hash)
                 :page  (current-page (.. js/window -location -hash))})
(def router (re-streamer/behavior-stream init-route))
(def router-outlet (re-streamer/map router :page))

(subscribe router #(set! (.. js/window -location -hash) (:route %)))

(defn navigate [route]
  (emit router {:route route
                :page  (current-page route)}))

(set! (.-onhashchange js/window) #(navigate (.. js/window -location -hash)))

;; === App Container ===

(defn app []
  [:div
   [:h2 "App Header"]
   [:button {:on-click #(navigate "#/home")} "Home"]
   [:button {:on-click #(navigate "#/about")} "About"]
   [:br]
   [@(:state router-outlet)]
   [:small "App Footer"]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
