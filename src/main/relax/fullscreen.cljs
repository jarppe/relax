(ns relax.fullscreen
  (:require [applied-science.js-interop :as j]
            [relax.state :as state]
            [relax.util :as u]))


(defn- set-fullscreen [on?]
  (if on?
    (do (u/add-class "fullscreen" "active")
        (-> (j/call (u/get-elem "wrapper") :requestFullscreen)
            (j/call :then (fn [_] (println "fullscreen: success")) (fn [_] (println "fullscreen: rejected")))))
    (do (u/remove-class "fullscreen" "active")
        (-> (j/call js/document :exitFullscreen)
            (j/call :then (fn [_] (println "fullscreen: success")) (fn [_] (println "fullscreen: rejected")))))))


(defn init! []
  (let [can-fullscreen? (some? (j/get js/document :exitFullscreen))
        is-fullscreen?  (some? (j/get js/document :fullscreenElement))]
    (when-not can-fullscreen?
      (u/set-attr "fullscreen" :display "none"))
    (when can-fullscreen?
      (state/on-change :is-fullscreen? set-fullscreen))
    (swap! state/app-state merge {:can-fullscreen? can-fullscreen?
                                  :is-fullscreen?  is-fullscreen?})))