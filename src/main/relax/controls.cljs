(ns relax.controls
  (:require [relax.state :refer [app-state]]
            [relax.util :as u :refer [js-get]]
            [relax.scenes :as scenes]))


(defn toggle-sound []
  (swap! app-state update :sound-on? not))


(defn toggle-fullscreen []
  (swap! app-state update :is-fullscreen? not))


(defn on-change-scene [change]
  (fn []
    (swap! app-state update :scene-id
           (fn [scene-id change]
             (let [scene-id (+ scene-id change)]
               (if (>= scene-id (count scenes/scenes))
                 0
                 (if (< scene-id 0)
                   (dec (count scenes/scenes))
                   scene-id))))
           change)))


(def on-prev (on-change-scene -1))
(def on-next (on-change-scene +1))


(defn on-keydown [^js e]
  (when-let [handler (case (js-get e :code)
                       "ArrowUp" toggle-fullscreen
                       "ArrowRight" on-next
                       "ArrowLeft" on-prev
                       "ArrowDown" toggle-sound
                       nil)]
    (.preventDefault e)
    (handler))
  nil)


(defn init! []
  (u/add-event-listener "fullscreen" :click toggle-fullscreen)
  (u/add-event-listener "sound" :click toggle-sound)
  (u/add-event-listener "prev" :click on-prev)
  (u/add-event-listener "next" :click on-next)
  (u/add-event-listener js/window :keydown on-keydown))
