(ns relax.app
  (:require [clojure.string :as str]
            [applied-science.js-interop :as j]
            [relax.state :refer [app-state]]
            [relax.fullscreen :as fullscreen]
            [relax.audio :as sound]
            [relax.controls :as controls]
            [relax.scenes :as scenes]))


(defonce init-app
  (delay (when-not (str/starts-with? (j/get-in js/window [:location :host]) "localhost")
           (println "init: registering worker...")
           (-> (j/call-in js/navigator [:serviceWorker :register] "worker.js" #js {:scope "./"})
               (j/call :then (fn [_] (println "init: worker registered")))
               (j/call :catch (fn [error] (println "init: worker failed" error)))))
         (println "init: init modules")
         (sound/init!)
         (fullscreen/init!)
         (controls/init!)
         (scenes/init!)
         (println "init: animation")
         (let [start-time (j/call-in js/window [:performance :now])]
           (j/call js/window :requestAnimationFrame
                   (fn animation [ts]
                     (scenes/on-tick (- ts start-time))
                     (j/call js/window :requestAnimationFrame animation))))))


; Start point of the app. In prod called just once from index.html, when developing
; called also when changed:

(defn ^:export run []
  @init-app)
