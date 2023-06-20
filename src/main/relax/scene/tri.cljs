(ns relax.scene.tri
  (:require [applied-science.js-interop :as j]
            [relax.svg :as svg]
            [relax.audio :as audio]
            [relax.util :as u]))


(def ^:const PI js/Math.PI)
(def ^:const sin js/Math.sin)
(def ^:const cos js/Math.cos)


(def ^:const PIx2   (* 2 PI))
(def ^:const PIp2   (* 0.5 PI))
(def ^:const PIx2p3 (/ PIx2 3.0))


(def ^:cont ball-count 25)
(def ^:const total-time (* 10 60 1000))
(def ^:const min-laps 10)
(def ^:const max-laps 20)


(def ^:const min-speed (/ (* PIx2 min-laps) total-time))
(def ^:const max-speed (/ (* PIx2 max-laps) total-time))
(def ^:const delta-speed (/ (- max-speed min-speed) ball-count))


(def tri-index->radius
  (let [min-radius 20.0
        radiuses   (vec (for [n (range ball-count)]
                          (+ min-radius (* n (/ (- 500.0 min-radius) ball-count)))))]
    (partial nth radiuses)))


(def tri-index->speed
  (let [speeds (vec (for [n (range ball-count)]
                      (+ min-speed (* n delta-speed))))]
    (partial nth speeds)))


(defn tri-angle [tri-index ts]
  (let [speed (tri-index->speed tri-index)]
    (* ts speed)))


(defn ball-pos [tri-index ball-index ts]
  (let [radius (tri-index->radius tri-index)
        speed  (tri-index->speed tri-index)
        angle  (+ (* ball-index PIx2p3)
                  (* ts speed))]
    [(* radius (sin angle))
     (* radius (cos angle))]))


(defn on-tick [scene-data ts]
  (let [{:keys [tris lines]} scene-data]
    (->> (map (fn [tri angle]
                (svg/set-attr tri :transform (str "rotate(-" angle ")")))
              tris
              (for [tri-index (range ball-count)]
                (-> (tri-angle tri-index ts)
                    (/ PI)
                    (* 180.0))))
         (dorun))
    #_(->> (map (fn [ball [x y]]
                  (svg/set-attr ball :cx x :cy y))
                balls
                (for [tri  (range ball-count)
                      ball (range 3)]
                  (ball-pos tri ball ts)))
           (dorun))
    (->> (map (fn [line [[x1 y1] [x2 y2]]]
                (svg/set-attr line :x1 x1 :y1 y1 :x2 x2 :y2 y2))
              lines
              (for [tri  (range (dec ball-count))
                    ball (range 3)]
                [(ball-pos tri ball ts)
                 (ball-pos (inc tri) ball ts)]))
         (dorun)))
  scene-data)


(defn on-resize [scene-data _]
  (let [elem   (:elem scene-data)
        scene  (:scene scene-data)
        width  (j/get elem :clientWidth)
        height (j/get elem :clientHeight)
        scale  (min (/ width 1000.0)
                    (/ height 1000.0))]
    (js/console.log `on-resize width "x" height "=>" scale)
    (svg/set-attr elem :viewBox (str "0 0 " width " " height))
    (svg/set-attr scene :transform (str "translate(" (* width 0.5) " " (* height 0.5) ") "
                                        "scale(" scale ") "
                                        "rotate(-90)"))
    scene-data))


(defn create-scene []
  (let [balls (vec (for [tri  (range ball-count)
                         ball (range 3)
                         :let [[x y] (ball-pos tri ball 0)]]
                     (svg/circle {:stroke-width 2
                                  :stroke       "hsl(257deg, 35%, 49%)"
                                  :fill         "hsl(257deg, 35%, 29%)"}
                                 x y 7)))
        tris  (vec (for [tri (range ball-count)]
                     (svg/g {:transform "rotate(0)"}
                            (svg/polyline {:stroke-width 1
                                           :stroke       "hsl(257deg, 35%, 29%)"
                                           :fill         "none"}
                                          (->> (range 4)
                                               (map (fn [ball-index]
                                                      (ball-pos tri ball-index 0)))))
                            (->> balls
                                 (drop (* 3 tri))
                                 (take 3)))))
        lines (vec (for [tri  (range (dec ball-count))
                         ball (range 3)
                         :let [[x1 y1] (ball-pos tri ball 0)
                               [x2 y2] (ball-pos (inc tri) ball 0)]]
                     (svg/line x1 y1 x2 y2)))
        scene (-> (svg/g
                   (svg/g {:stroke-width 1
                           :stroke       "hsl(257deg, 35%, 49%)"
                           :fill         "none"}
                          lines)
                   (svg/g tris)))]
    {:elem  (svg/svg {:class   "ar-1-1"
                      :viewBox "0 0 0 0"}
                     scene)
     :scene scene
     :tris  tris
     :balls balls
     :lines lines}))
