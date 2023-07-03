(ns relax.worker.cache
  (:require [promesa.core :as p]
            [relax.util :refer [js-get js-get-in]]))


(defn cache []
  (js/caches.open "v2"))


(defn add-resources-to-cache [resources]
  (-> (cache)
      (p/then (fn [^js cache] (.addAll cache (clj->js resources))))))


(defn put-in-cache [req resp]
  (-> (cache)
      (p/then (fn [^js cache] (.put cache req resp)))))


(defn cache-fetch [event]
  (let [request (js-get event "request")]
    (-> (p/resolved request)
        (p/then js/fetch)
        (p/then (fn [^js response]
                  (-> (put-in-cache request (.clone response))
                      (p/then (fn [_] response)))))
        (p/catch (fn [error]
                   (js/console.log "js/fetch: error" error)
                   (js/caches.match request)))
        (p/catch (fn [error]
                   (js/console.error "cache-fetch: failed" error)
                   (throw error))))))


(defn enable-navigation-preload []
  (when-let [preload (js-get-in js/self ["registration" "navigationPreload"])]
    (.enable preload)))
