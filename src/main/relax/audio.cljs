(ns relax.audio
  (:require [applied-science.js-interop :as j]
            ["Howl" :as Howl]
            [relax.state :as state]
            [relax.util :as u]))


(def audio-urls (->> ["G1.flac" "A1.flac" "B1.flac"
                      "D2.flac" "F2.flac" "G2.flac" "B2.flac"
                      "D3.flac" "E3.flac" "G3.flac" "A3.flac" "B3.flac"
                      "D4.flac" "E4.flac" "F4.flac" "G4.flac" "A4.flac" "B4.flac"
                      "D5.flac" "E5.flac" "F5.flac" "G5.flac" "A5.flac" "B5.flac" "C6.flac"]
                     (mapv (fn [f] (str "audio/nylon-guitar/" f)))))


(def audio-elements (->> audio-urls
                         (mapv (fn [f] (Howl. (j/obj :src f
                                                     :volume 0.4))))))


(defn play [sound-index balance]
  (when (:sound-on? @state/app-state)
    (doto (nth audio-elements sound-index)
      (j/call :stereo balance)
      (j/call :play))))



(defn set-sound-on [on?]
  (if on?
    (u/add-class "sound" "active")
    (u/remove-class "sound" "active")))


(defn init! []
  (state/on-change :sound-on? set-sound-on)
  (swap! state/app-state assoc :sound-on? false))
