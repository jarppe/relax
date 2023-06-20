(ns relax.worker.cache
  (:require [applied-science.js-interop :as j]
            [promesa.core :as p]))


(defn cache []
  (j/call js/caches :open "v2"))


(defn add-resources-to-cache [resources]
  (-> (cache)
      (p/then (fn [cache] (j/call cache :addAll (clj->js resources))))))


(defn put-in-cache [req resp]
  (-> (cache)
      (p/then (fn [cache] (j/call cache :put req resp)))))


(defn cache-fetch [event]
  (let [request (j/get event :request)]
    (-> (p/resolved request)
        (p/then js/fetch)
        (p/then (fn [response]
                  (-> (put-in-cache request (j/call response :clone))
                      (p/then (fn [_] response)))))
        (p/catch (fn [error]
                   (js/console.log "js/fetch: error" error)
                   (j/call js/caches :match request)))
        (p/catch (fn [error]
                   (js/console.error "cache-fetch: failed" error)
                   (throw error))))))


(defn enable-navigation-preload []
  (when-let [preload (j/get-in js/self [:registration :navigationPreload])]
    (j/call preload :enable)))
