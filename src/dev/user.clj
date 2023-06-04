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


  ;
  )