(ns reagent-example.prod
  (:require
    [reagent-example.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
