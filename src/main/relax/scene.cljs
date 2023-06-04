(ns relax.scene
  (:require [applied-science.js-interop :as j]
            [relax.svg :as svg]
            ["Howl" :as Howl]
            [relax.toggle :as toggle]
            [relax.util :as u]))


(def audio-elements (->> ["G1.flac" "A1.flac" "B1.flac"
                          "D2.flac" "F2.flac" "G2.flac" "B2.flac"
                          "D3.flac" "E3.flac" "G3.flac" "A3.flac" "B3.flac"
                          "D4.flac" "E4.flac" "F4.flac" "G4.flac" "A4.flac" "B4.flac"
                          "D5.flac" "E5.flac" "F5.flac" "G5.flac" "A5.flac" "B5.flac" "C6.flac"]
                         (mapv (fn [f]
                                 (Howl. (j/obj :src (str "audio/nylon-guitar/" f)))))))


(def ^:cont ball-count (count audio-elements))
(def ^:const total-time (* 5 60 1000))
(def ^:const min-laps 20)
(def ^:const add-laps 0.5)


(defn on-resize [{:keys [svg scene]}]
  (let [width  (j/get svg :clientWidth)
        height (j/get svg :clientHeight)
        scale  (min (/ width 2000.0)
                    (/ height 1000.0))]
    (js/console.log `on-resize width "x" height "=>" scale)
    (svg/set-attr svg :viewBox (str "0 0 " width " " height))
    (svg/set-attr scene :transform (str "translate(" (* width 0.5) " " height ") "
                                        "scale(" scale ")"
                                        "rotate(-90)"))))


(defn ts->angle [ts]
  (let [a (mod ts 360.0)]
    (cond
      (< a 90.0) a
      (< a 270.0) (- 90.0 (- a 90.0))
      :else (+ -90.0 (- a 270.0)))))


(def orbit-stroke-scale (comp (u/bound 10 60) (u/scaler [0 180] [60 10])))
(def ball-stroke-scale (comp (u/bound 10 90) (u/scaler [0 180] [10 90])))
(def ball-fill-scale (u/scaler [0 (dec ball-count)] [20 50]))


(defn on-tick [{:keys [start balls orbits]} now]
  (let [ts        (- now start)
        audio-on? (toggle/audio-on?)]
    (doseq [n (range ball-count)]
      (let [ball  (nth balls n)
            orbit (nth orbits n)
            speed (js/parseFloat (svg/get-attr ball :speed))
            a     (-> (* ts speed)
                      (mod 360.0))
            rot   (- a 180.0)
            dir   (if (pos? rot) "p" "n")
            angle (ts->angle (* ts speed))]
        (svg/set-attr orbit
                      :stroke (str "hsl(0 0% " (orbit-stroke-scale (mod a 180)) "%)"))
        (svg/set-attr ball
                      :transform (str "rotate(" angle ")")
                      :stroke (str "hsl(0 0% " (ball-stroke-scale (mod a 180)) "%)")
                      :fill (str "hsl(320 100% " (ball-fill-scale n) "%)"))
        (when (not= (svg/get-attr ball :dir) dir)
          (svg/set-attr ball :dir dir)
          (when audio-on?
            (doto (nth audio-elements n)
              (j/call :stereo (if (= dir "p") -0.9 0.9))
              (j/call :play))))))))


(defn ball-index->orbit-radius [n]
  (+ 100 (* n (/ 1000 (+ ball-count 2)))))


(defn ball-index->speed [n]
  (-> (+ min-laps (* n add-laps))
      (* 180.0)
      (/ total-time)))


(defn make-ball [n]
  (let [orbit-radius (ball-index->orbit-radius n)
        speed        (ball-index->speed n)]
    (svg/g {:speed speed}
           (svg/circle {:cx orbit-radius
                        :cy 0
                        :r  15}))))


(defn make-orbit [n]
  (let [r (ball-index->orbit-radius n)]
    (svg/path {:stroke (str "hsl(0 0% 30%)")
               :d      (str "M 0," (- r) " A " r " " r " 180 1 1 0," r)})))


(defn create-scene [start-timestamp]
  (let [balls  (mapv make-ball (range ball-count))
        orbits (mapv make-orbit (range ball-count))
        scene  (-> (svg/g)
                   (svg/append (svg/g {:stroke-width 1
                                       :fill         "none"}
                                      orbits))
                   (svg/append (svg/g {:stroke-width 2}
                                      balls)))]
    {:svg    (svg/svg {:viewBox "0 0 0 0"} scene)
     :start  start-timestamp
     :scene  scene
     :balls  balls
     :orbits orbits}))
