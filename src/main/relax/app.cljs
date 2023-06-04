(ns relax.app
  (:require [applied-science.js-interop :as j]
            [relax.scene :as scene]
            [relax.toggle :as toggle]
            [relax.util :as u]))


; Save start time. This helps in dev. When changes are hot-reloaded, the
; visualizations continue without jump because we have saved the start time
; of the initial rendering.
(defonce start-time (j/call-in js/window [:performance :now]))


; Hold controller in atom. Allows easy unregistering of DOM listeners:
(defonce ctrl-holder (atom nil))


; Atom to hole run atom. Incepton style atom in atom. The inner atom is
; passed to animation loop.
(defonce run-holder (atom nil))


; Make animation function. Accepts an atom to control when to exit, and a
; scnene to animate:
(defn make-animation [run? scene]
  (fn animation [ts]
    (when @run?
      (scene/on-tick scene ts)
      (j/call js/window :requestAnimationFrame animation))))


; Cleanup everything:
(defn cleanup []
  (when-let [run? @run-holder]
    (reset! run? false)
    (reset! run-holder nil))
  (when-let [controller @ctrl-holder]
    (j/call controller :abort)
    (reset! ctrl-holder nil))
  (u/set-text "app" ""))


; Create and start everything:
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


(defn init-worker []
  (println "init-worker: registering...")
  (-> (j/call-in js/navigator [:serviceWorker :register] "worker.js" #js {:scope "./"})
      (j/call :then (fn [_] (println "init-worker: registered")))
      (j/call :catch (fn [error] (println "init-worker: failed" error)))))


; Start point of the app. Called just once from index.html:
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn ^:export run []
  (init-worker)
  (cleanup)
  (start))
