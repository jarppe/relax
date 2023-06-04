(ns relax.worker.cache
  (:require [applied-science.js-interop :as j]))


(defn cache []
  (j/call js/caches :open "v1"))


(defn add-resources-to-cache [resources]
  (-> (cache)
      (j/call :then (fn [cache]
                      (j/call cache :addAll (clj->js resources))))
      (j/call :then (fn [r]
                      (js/console.log "add-resources-to-cache: addAll success")
                      r))
      (j/call :catch (fn [error]
                       (js/console.log "add-resources-to-cache: addAll fail" error)))))


(defn put-in-cache [req resp]
  (-> (cache)
      (j/call :then (fn [cache] (j/call cache :put req resp)))
      (j/call :then (fn [r]
                      (js/console.log "put-in-cache: success")
                      r))
      (j/call :catch (fn [error]
                       (js/console.log "put-in-cache: fail" error)))))


(defn cache-first [event]
  (let [request (j/get event :request)]
    (-> (j/call js/caches :match request)
        (j/call :then (fn [response]
                        (if (some? response)
                          response
                          (-> (js/fetch request)
                              (j/call :then (fn [response]
                                              (-> (put-in-cache request (j/call response :clone))
                                                  (j/call :then (fn [_]
                                                                  response)))))
                              (j/call :catch (fn [error]
                                               (js/console.log "js/fetch: error" error)
                                               (throw error)))))))
        (j/call :catch (fn [error]
                         (js/console.error "cache-first: failed" error)
                         (throw error))))))


(defn enable-navigation-preload []
  (when-let [preload (j/get-in js/self [:registration :navigationPreload])]
    (j/call preload :enable)))
