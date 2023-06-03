(ns relax.audio-on
  (:require [applied-science.js-interop :as j]))


(defonce -audio-on? (atom false))


(defn toggle-audio []
  (let [on?  (swap! -audio-on? not)
        elem (j/call js/document :getElementById "audio-on")]
    (j/call elem :setAttribute "data-audio" (if on? "on" "off"))
    (j/assoc! elem :textContent (if on? "Audio: ON" "Audio: OFF"))))


(defn audio-on? []
  @-audio-on?)
