(ns relax.app
  (:require [relax.fullscreen :as fullscreen]
            [relax.audio :as sound]
            [relax.controls :as controls]
            [relax.scenes :as scenes]
            [relax.util :refer [js-get]]))


(defonce init-app
  (delay (println "init: registering worker...")
         (-> (js-get js/navigator "serviceWorker")
             (.register "worker.js" #js {:scope "./"})
             (.then (fn [_] (println "init: worker registered")))
             (.catch (fn [error] (println "init: worker failed" error))))
         (println "init: init modules")
         (sound/init!)
         (fullscreen/init!)
         (controls/init!)
         (scenes/init!)
         (println "init: animation")
         (let [start-time (-> (js-get js/window "performance")
                              (.now))]
           (js/window.requestAnimationFrame
            (fn animation [ts]
              (scenes/on-tick (- ts start-time))
              (js/window.requestAnimationFrame animation))))))


; Start point of the app. In prod called just once from index.html, when developing
; called also when changed:

(defn ^:export run []
  @init-app)
