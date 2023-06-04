(ns relax.app
  (:require [applied-science.js-interop :as j]
            [relax.scene :as scene]
            [relax.toggle :as toggle]
            [relax.util :as u]))


(defonce start-time (j/call-in js/window [:performance :now]))


(defonce ctrl-holder (atom nil))
(defonce run-holder (atom nil))


(defn make-animation [run? scene]
  (fn animation [ts]
    (when @run?
      (scene/on-tick scene ts)
      (j/call js/window :requestAnimationFrame animation))))


(defn cleanup []
  (when-let [run? @run-holder]
    (reset! run? false)
    (reset! run-holder nil))
  (when-let [controller @ctrl-holder]
    (j/call controller :abort)
    (reset! ctrl-holder nil))
  (u/set-text "app" ""))


(defn start []
  (let [controller (js/AbortController.)
        opts       (j/obj :signal (j/get controller :signal))
        run?       (atom true)
        scene      (scene/create-scene start-time)]
    (reset! ctrl-holder controller)
    (reset! run-holder run?)
    (j/call js/window :addEventListener "resize" (fn [_] (scene/on-resize scene)) opts)
    (j/call js/document :addEventListener "click" toggle/click opts)
    (j/call (u/get-elem "app") :appendChild (:svg scene))
    (toggle/init)
    (scene/on-resize scene)
    (j/call js/window :requestAnimationFrame (make-animation run? scene))))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn ^:export run []
  (cleanup)
  (start))
