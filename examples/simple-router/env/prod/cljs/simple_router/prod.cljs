(ns simple-router.prod
  (:require
    [simple-router.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
