(ns relax.scenes
  (:require [relax.state :as state]
            [relax.util :as u :refer [js-set]]
            [relax.scene.pendulum :as pendulum]
            [relax.scene.wind :as wind]
            [relax.scene.tri :as tri]))


(def scenes [{:name         "pendulum"
              :create-scene pendulum/create-scene
              :on-resize    pendulum/on-resize
              :on-tick      pendulum/on-tick}
             {:name         "wind"
              :create-scene wind/create-scene
              :on-resize    wind/on-resize
              :on-tick      wind/on-tick}
             {:name         "tris"
              :create-scene tri/create-scene
              :on-resize    tri/on-resize
              :on-tick      tri/on-tick}])


(defonce current-scene-id (atom nil))
(defonce current-scene-data (atom nil))


(defn swap-scene [scene-id]
  (js/console.log "swap-scene:" scene-id)
  (let [scene      (nth scenes scene-id)
        scene-data ((:create-scene scene))]
    (reset! current-scene-id scene-id)
    (reset! current-scene-data scene-data)
    (u/set-text "scene-name" (:name scene))
    (-> (u/clear-elem "app")
        (u/append (:elem scene-data)))
    ((:on-resize scene) scene-data nil)
    (js-set js/location "hash" (:name scene))))


(defn on-tick [ts]
  (let [scene-id @current-scene-id
        scene    (nth scenes scene-id)]
    (swap! current-scene-data (:on-tick scene) ts)))


(defn on-resize [e]
  (let [scene-id @current-scene-id
        scene    (nth scenes scene-id)]
    (swap! current-scene-data (:on-resize scene) e)))


(defn init! []
  (u/add-event-listener js/window :resize on-resize)
  (state/on-change :scene-id swap-scene)
  (let [scene-name (subs js/location.hash 1)
        scene-id   (some (fn [scene-index]
                           (when (-> (nth scenes scene-index)
                                     :name
                                     (= scene-name))
                             scene-index))
                         (range (count scenes)))]
    (swap! state/app-state assoc :scene-id (or scene-id 0))))
