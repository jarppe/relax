(ns user
  (:require [shadow.cljs.devtools.api :as shadow]))


(defn repl [build-id]
  (shadow/repl build-id))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn repl-web [] (repl :web))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn repl-node [] (repl :node))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn repl-api [] (repl :functions))


(comment

  (shadow/compile :web)

  ;
  )