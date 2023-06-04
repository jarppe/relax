(ns web
  (:require [clojure.string :as str]
            [applied-science.js-interop :as j]))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (println "user.cljs: init"))


(comment


  (def note (->> ["C" "D" "E" "F" "G" "A" "B"]
                 (map-indexed (fn [n note] [note n]))
                 (into {})))


  (defn scaler [[from to] [min max]]
    (let [sdiff (double (- to from))
          tdiff (double (- max min))]
      (fn [v]
        (+ min (* tdiff (/ (- v from) sdiff))))))

  (let [scale (scaler [100 200] [200 0])]
    (scale 150))

  (println "  a:     x:")
  (doseq [a [0 45 90 135 180 225 270 315 360 405]]
    (println "  "))

  ;
  )
