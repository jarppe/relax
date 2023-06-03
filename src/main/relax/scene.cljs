(ns relax.scene
  (:require [applied-science.js-interop :as j]
            [relax.svg :as svg]
            ["Howl" :as Howl]
            [relax.audio-on :as audio-on]
            [relax.util :as u]))


(def audio (->> ["G1.flac" "A1.flac" "B1.flac" "D2.flac" "F2.flac" "G2.flac" "B2.flac" "D3.flac" "E3.flac" "G3.flac" "A3.flac" "B3.flac" "D4.flac" "E4.flac" "F4.flac" "G4.flac" "A4.flac" "B4.flac" "D5.flac" "E5.flac" "F5.flac" "G5.flac" "A5.flac" "B5.flac" "C6.flac"]
                (mapv (fn [f]
                        (Howl. (j/obj :src (str "audio/nylon-guitar/" f)))))))


(def ^:cont ball-count (count audio))
(def ^:const total-time (* 5 60 1000))
(def ^:const min-laps 20)
(def ^:const add-laps 0.5)


(defn on-resize [state]
  (let [svg    (:svg state)
        width  (j/get svg :clientWidth)
        height (j/get svg :clientHeight)
        scale  (/ width 2000.0)
        scene  (:scene state)]
    (js/console.log `on-resize width "x" height "=>" scale)
    (svg/set-attr svg :viewBox (str "0 0 " width " " height))
    (svg/set-attr scene :transform (str "translate(" (* width 0.5) " " height ") "
                                        "scale(" scale ")"
                                        "rotate(-90)"))))


(def orbit-stroke-scale (comp (u/bound 10 60) (u/scaler [0 180] [60 10])))
(def ball-stroke-scale (comp (u/bound 10 90) (u/scaler [0 180] [10 90])))
(def ball-fill-scale (u/scaler [0 (dec ball-count)] [20 50]))


(defn on-tick [{:keys [start balls orbits]} now]
  (let [ts (- now start)]
    (doseq [n (range ball-count)]
      (let [audio-on? (audio-on/audio-on?)
            ball      (nth balls n)
            orbit     (nth orbits n)
            speed     (js/parseFloat (svg/get-attr ball :speed))
            a         (-> (* ts speed)
                          (mod 360.0))
            rot       (- a 180.0)
            dir       (if (pos? rot) "p" "n")
            angle     (-> rot (abs) (- 90.0))]
        (svg/set-attr orbit
                      :stroke (str "hsl(0 0% " (orbit-stroke-scale (mod a 180)) "%)"))
        (svg/set-attr ball
                      :transform (str "rotate(" angle ")")
                      :stroke (str "hsl(0 0% " (ball-stroke-scale (mod a 180)) "%)")
                      :fill (str "hsl(320 100% " (ball-fill-scale n) "%)"))
        (when (not= (svg/get-attr ball :dir) dir)
          (svg/set-attr ball :dir dir)
          (when audio-on?
            (let [ndx   (js/parseInt (svg/get-attr ball :ndx))
                  sound ^js (nth audio ndx)]
              (.stereo sound (if (= dir "p") -0.9 0.9))
              (.play sound))))))))


(defn ball-index->r [n]
  (+ 100 (* n (/ 1000 (+ ball-count 2)))))


(defn n->speed [n]
  (-> (+ min-laps (* n add-laps))
      (* 180.0)
      (/ total-time)))


(defn make-ball [n r speed]
  (-> (svg/circle {:cx           r
                   :cy           0
                   :r            15
                   :stroke-width 2})
      (svg/g)
      (svg/set-attr :speed speed)
      (svg/set-attr :ndx n)))


(defn make-balls []
  (->> (range ball-count)
       (mapv (fn [n]
               (make-ball n (ball-index->r n) (n->speed n))))))

; hsl (0 0% 10%);

(defn make-orbit [n]
  (let [r (ball-index->r n)]
    (svg/path {:stroke (str "hsl(0 0% 30%)")
               :d      (str "M 0," (- r) " A " r " " r " 180 1 1 0," r)})))


(defn make-orbits []
  (->> (range ball-count)
       (mapv make-orbit)))


(defn create-scene [start]
  (let [scene  (svg/g)
        balls  (make-balls)
        orbits (make-orbits)]
    (svg/append scene (svg/g {:stroke-width 1
                              :fill         "none"}
                             orbits))
    (svg/append* scene balls)
    {:svg    (svg/svg {:viewBox "0 0 0 0"} scene)
     :start  start
     :scene  scene
     :balls  balls
     :orbits orbits}))


;; :width           200
;; :height          200
;; :stroke          "hsl(8, 100%, 67%)"
;; :fill            "none"
;; :stroke-width    3
;; :stroke-linejoin "round"