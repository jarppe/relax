(ns relax.toggle
  (:require [applied-science.js-interop :as j]))


(defonce -audio (atom false))
(defonce -fullscreen (atom false))


(defn audio-on? []
  @-audio)


(defn- toggle-fullscreen [_]
  (let [on?  (swap! -fullscreen not)
        elem (j/call js/document :getElementById "fullscreen")]
    (j/call elem :setAttribute "data-value" (if on? "on" "off"))
    (j/assoc! elem :textContent (if on? "Fullscreen: ON" "Fullscreen: OFF"))
    (if on?
      (do (println "Fullscreen: ON")
          (-> (j/call js/document :getElementById "app")
              (j/call :requestFullscreen)))
      (do (println "Fullscreen: Off")
          (-> (j/call js/document :exitFullscreen))))))


(defn- toggle-audio [_]
  (let [on?  (swap! -audio not)
        elem (j/call js/document :getElementById "audio")]
    (j/call elem :setAttribute "data-value" (if on? "on" "off"))
    (j/assoc! elem :textContent (if on? "Audio: ON" "Audio: OFF"))))


(defn click [e]
  (let [width (-> (j/call js/document :getElementById "app")
                  (j/get :clientWidth))
        x     (j/get e :clientX)]
    (if (< x (* width 0.5))
      (toggle-fullscreen e)
      (toggle-audio e))))



