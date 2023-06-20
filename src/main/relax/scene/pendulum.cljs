(ns relax.scene.pendulum
  (:require [applied-science.js-interop :as j]
            [relax.svg :as svg]
            [relax.audio :as audio]
            [relax.util :as u]))


(def ^:cont ball-count (count audio/audio-elements))
(def ^:const total-time (* 5 60 1000))
(def ^:const min-laps 20)
(def ^:const add-laps 0.5)


(defn on-resize [scene-data _]
  (let [elem   (:elem scene-data)
        scene  (:scene scene-data)
        width  (j/get elem :clientWidth)
        height (j/get elem :clientHeight)
        scale  (min (/ width 2000.0)
                    (/ height 1000.0))]
    (js/console.log `on-resize width "x" height "=>" scale)
    (svg/set-attr elem :viewBox (str "0 0 " width " " height))
    (svg/set-attr scene :transform (str "translate(" (* width 0.5) " " height ") "
                                        "scale(" scale ")"
                                        "rotate(-90)"))
    scene-data))


(defn angle->ball-rotation [angle]
  (cond
    (< angle 90.0) angle
    (< angle 270.0) (- 90.0 (- angle 90.0))
    :else (+ -90.0 (- angle 270.0))))


(def angle->phase-dist
  (let [phase-dist-scale (comp (u/bound 0.0 100.0)
                               (u/scaler [0.0 90.0] [100.0 0.0]))]
    (fn [angle]
      (phase-dist-scale (cond
                          (< 90.0 angle 180.0) (- angle 90.0)
                          (< 270.0 angle 360.0) (- angle 270)
                          :else 100.0)))))



(def phase-dist->ball-stroke-luminosity (u/scaler [0.0 100.0] [40.0 100.0]))
(def phase-dist->orbit-luminosity (u/scaler [0.0 100.0] [20.0 60.0]))


(defn on-tick [scene-data ts]
  (let [{:keys [balls orbits]} scene-data]
    (doseq [n (range ball-count)]
      (let [ball       (nth balls n)
            orbit      (nth orbits n)
            speed      (-> (svg/get-attr ball :speed)
                           (js/parseFloat))
            angle      (mod (* ts speed) 360.0)
            phase      (if (< 90 angle 270) "d" "r")
            phase-dist (angle->phase-dist angle)]
        (svg/set-attr orbit :stroke (str "hsl(0 0% " (phase-dist->orbit-luminosity phase-dist) "%)"))
        (svg/set-attr ball
                      :transform (str "rotate(" (angle->ball-rotation angle) ")")
                      :stroke (str "hsl(0 0% " (phase-dist->ball-stroke-luminosity phase-dist) "%)"))
        (when (not= (svg/get-attr ball :phase) phase)
          (svg/set-attr ball :phase phase)
          (audio/play n (if (= phase "r") -0.9 0.9)))))
    scene-data))


(defn- ball-index->orbit-radius [n]
  (+ 100 (* n (/ 1000 (+ ball-count 2)))))


(defn- ball-index->speed [n]
  (-> (+ min-laps (* n add-laps))
      (* 180.0)
      (/ total-time)))


(def ball-index->ball-fill (u/scaler [0 ball-count] [200.0 300.0]))


(defn- make-ball [n]
  (let [orbit-radius (ball-index->orbit-radius n)
        speed        (ball-index->speed n)]
    (svg/g {:speed speed}
           (svg/circle {:fill (str "hsl(" (ball-index->ball-fill n) " 100% 50%)")}
                       orbit-radius 0 15))))


(defn- make-orbit [n]
  (let [r (ball-index->orbit-radius n)]
    (svg/path {:stroke (str "hsl(0 0% 30%)")} (str "M 0," (- r) " A " r " " r " 180 1 1 0," r))))


(defn create-scene []
  (let [balls  (mapv make-ball (range ball-count))
        orbits (mapv make-orbit (range ball-count))
        scene  (-> (svg/g)
                   (svg/append (svg/g {:stroke-width 1
                                       :fill         "none"}
                                      orbits))
                   (svg/append (svg/g {:stroke-width 2}
                                      balls)))]
    {:elem   (svg/svg {:class   "ar-2-1"
                       :viewBox "0 0 0 0"}
                      scene)
     :scene  scene
     :balls  balls
     :orbits orbits}))
