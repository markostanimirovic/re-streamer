(ns typeahead.prod
  (:require
    [typeahead.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
