(ns relax.worker
  (:require [applied-science.js-interop :as j]
            [relax.worker.cache :as cache]
            [relax.audio :as audio]))


(js/console.info "worker: initializing...")


(j/call js/self :addEventListener "activate"
        (fn [event]
          (js/console.info "worker: activate")
          (j/call event :waitUntil (cache/enable-navigation-preload))))


(j/call js/self :addEventListener "install"
        (fn [event]
          (js/console.info "worker: install")
          (j/call event :waitUntil (cache/add-resources-to-cache (concat ["./index.html"
                                                                          "./styles.css"
                                                                          "./app.js"
                                                                          "./shared.js"
                                                                          "./favicon.ico"]
                                                                         (map (fn [f] (str "./" f))
                                                                              audio/audio-urls))))))


(j/call js/self :addEventListener "fetch"
        (fn [event]
          (j/call event :respondWith (cache/cache-fetch event))))


(j/call js/self :addEventListener "message"
        (fn [event]
          (js/console.info "worker: message" event)))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (js/console.info "worker: init"))
