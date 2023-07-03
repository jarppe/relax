(ns relax.worker
  (:require [relax.worker.cache :as cache]
            [relax.audio :as audio]))


(js/console.info "worker: initializing...")


(.addEventListener js/self "activate"
                   (fn [^js event]
                     (js/console.info "worker: activate")
                     (.waitUntil event (cache/enable-navigation-preload))))


(.addEventListener js/self "install"
                   (fn [^js event]
                     (js/console.info "worker: install")
                     (.waitUntil event (cache/add-resources-to-cache (concat ["./index.html"
                                                                              "./styles.css"
                                                                              "./app.js"
                                                                              "./shared.js"
                                                                              "./favicon.ico"]
                                                                             (map (fn [f] (str "./" f))
                                                                                  audio/audio-urls))))))


(.addEventListener js/self "fetch"
                   (fn [^js event]
                     (js/console.log "worker: fetch" event)
                     (.respondWith event (cache/cache-fetch event))))


(.addEventListener js/self  "message"
                   (fn [event]
                     (js/console.info "worker: message" event)))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (js/console.info "worker: init"))
