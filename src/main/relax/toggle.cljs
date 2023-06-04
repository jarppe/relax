(ns relax.toggle
  (:require [applied-science.js-interop :as j]
            [relax.util :as u]))


(defonce -audio (atom false))
(defonce -fullscreen (atom false))


(defn audio-on? []
  @-audio)


(defn- set-fullscreen [on?]
  (-> (u/get-elem "fullscreen")
      (u/set-attr :data-value (if on? "on" "off"))
      (u/set-text (if on? "Fullscreen: ON" "Fullscreen: OFF")))
  (if on?
    (j/call (u/get-elem "wrapper") :requestFullscreen)
    (-> (j/call js/document :exitFullscreen)
        (j/call :then identity identity))))


(defn- set-audio [on?]
  (-> (u/get-elem "audio")
      (u/set-attr :data-value (if on? "on" "off"))
      (u/set-text (if on? "Audio: ON" "Audio: OFF"))))


(defn- toggle-fullscreen [_]
  (let [on? (swap! -fullscreen not)]
    (set-fullscreen on?)))


(defn- toggle-audio [_]
  (let [on? (swap! -audio not)]
    (set-audio on?)))


(defn click [e]
  (let [width (-> (u/get-elem "app")
                  (j/get :clientWidth))
        x     (j/get e :clientX)]
    (if (< x (* width 0.5))
      (toggle-fullscreen e)
      (toggle-audio e))))


(defn init []
  (set-fullscreen @-fullscreen)
  (set-audio @-audio))
