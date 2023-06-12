(ns relax.scenes
  (:require [relax.state :as state]
            [relax.pendulum :as pendulum]
            [relax.util :as u]))


(def scenes [{:create-scene pendulum/create-scene
              :on-resize    pendulum/on-resize
              :on-tick      pendulum/on-tick}])


(def current-scene (atom nil))


(defn swap-scene [scene-id]
  (let [{:keys [create-scene on-resize on-tick]} (nth scenes scene-id)
        scene-data                               (create-scene)]
    (reset! current-scene {:scene     scene-data
                           :on-resize on-resize
                           :on-tick   on-tick})
    (-> (u/clear-elem "app")
        (u/append (:elem scene-data)))
    (on-resize scene-data nil)))


(defn on-tick [ts]
  (let [{:keys [on-tick scene]} @current-scene]
    (on-tick scene ts)))


(defn on-resize [e]
  (let [{:keys [on-resize scene]} @current-scene]
    (on-resize scene e)))


(defn init! []
  (u/add-event-listener js/window :resize on-resize)
  (state/on-change :scene-id swap-scene)
  (swap! state/app-state assoc :scene-id 0))
