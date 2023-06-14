(ns relax.scene.wind
  (:require [applied-science.js-interop :as j]
            [relax.svg :as svg]
            [relax.util :as u]))


(defn on-resize [{:keys [elem scene]} _]
  (let [width  (j/get elem :clientWidth)
        height (j/get elem :clientHeight)
        scale  (min (/ width 2000.0)
                    (/ height 1000.0))]
    (js/console.log `on-resize width "x" height "=>" scale)
    (svg/set-attr elem :viewBox (str "0 0 " width " " height))
    (svg/set-attr scene :transform (str "scale(" scale ")"))))


(def line-length-scaler (comp (u/bound 0.0 1.0)
                              (u/scaler [0.0 400.0] [0.0 1.0])))


(defn on-tick [scene-data ts]
  (let [wind-dx (:wind-dx scene-data)
        wind-x  (+ (:wind-x scene-data) wind-dx)
        wind-dy (:wind-dy scene-data)
        wind-y  (+ (:wind-y scene-data) wind-dy)]
    (doseq [{:keys [x y line]} (:lines scene-data)]
      (let [dx     (- wind-x x)
            dy     (- wind-y y)
            dist   (js/Math.sqrt (+ (* dx dx) (* dy dy)))
            scale  (line-length-scaler dist)
            rotate (-> (js/Math.atan2 dy dx)
                       (* 180.0)
                       (/ js/Math.PI)
                       (- 90.0))]
        (svg/set-attr line :transform (str "scale(" scale ") rotate(" rotate ")"))))
    (-> scene-data
        (assoc :wind-x wind-x)
        (assoc :wind-y wind-y)
        (assoc :wind-dx (if (or (< wind-x 0) (> wind-x 2000))
                          (- wind-dx)
                          wind-dx))
        (assoc :wind-dy (if (or (< wind-y 0) (> wind-y 1000))
                          (- wind-dy)
                          wind-dy)))))


(defn create-scene []
  (let [lines (for [x    (range 100 2000 50)
                    y    (range 100 1000 50)
                    :let [frame (svg/g {:transform (str "translate(" x " " y ")")})
                          line (svg/g {:transform "scale(0) rotate(0)"}
                                      (svg/line {:x1 0
                                                 :y1 0
                                                 :x2 0
                                                 :y2 30}))]]
                {:x     x
                 :y     y
                 :frame (svg/append frame line)
                 :line  line})
        scene (svg/g {:stroke-width 1
                      :stroke       "white"}
                     (map :frame lines))]
    {:elem    (svg/svg {:class   "ar-2-1"
                        :viewBox "0 0 0 0"}
                       scene)
     :scene   scene
     :wind-x  1000.0
     :wind-dx 0.7
     :wind-y  500.0
     :wind-dy 0.4
     :lines   lines}))
