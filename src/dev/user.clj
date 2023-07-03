(ns user
  (:require [shadow.cljs.devtools.api :as shadow]))


(defn repl [build-id]
  (shadow/repl build-id))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn repl-web [] (repl :web))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn repl-node [] (repl :node))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn repl-api [] (repl :functions))


(comment

  (shadow/compile :web)


  (defn a->x [a]
    (let [a (mod a 360)]
      (cond
        (< a 90) a
        (< a 270) (- 90 (- a 90))
        :else (+ -90 (- a 270)))))


  (add-watch #'a->x ::test (fn [_ _ _ f]
                             (println "   a:    x:")
                             (doseq [a [0 45 89 90 91 135 180 225 269 270 271 315 360 405]]
                               (println (format "  %3d   %3d" a (f a))))))


  (require 'clojure.math)


  (def ^:const PI clojure.math/PI)
  (def ^:const sin clojure.math/sin)
  (def ^:const cos clojure.math/cos)


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
    (let [radiuses (vec (for [n (range ball-count)]
                          (+ 50.0 (* n (/ 450.0 (+ ball-count 2.0))))))]
      (partial nth radiuses)))


  (def tri-index->speed
    (let [speeds (vec (for [n (range ball-count)]
                        (+ min-speed (* n delta-speed))))]
      (partial nth speeds)))


  (defn ball-pos [tri-index ball-index ts]
    (let [radius (tri-index->radius tri-index)
          speed  (tri-index->speed tri-index)
          angle  (+ (* ball-index PIx2p3)
                    (* ts speed))]
      [(* radius (sin angle))
       (* radius (cos angle))]))


  (defn foo [& attrs]
    attrs)

  (->> {:a 1
        :b 2
        :c 3}
       (mapcat identity)
       (apply foo))

  (foo :a 1 :b 2)
  ;
  )