(ns web
  (:require [clojure.string :as str]
            [goog.object :as g]))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (println "user.cljs: init"))


(comment

  (let [elem #js {:foo {:a 1}}]
    (g/set elem "foo" {:b 2})
    (type (g/get elem "foo")))

  (let [elem #js {}]
    (g/set elem :foo 42)
    (g/get elem :foo))
  ;

  (subs js/location.hash 1))
