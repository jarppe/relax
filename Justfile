set dotenv-load := true
project := "training-2023-05-32"


help:
  @just --list


# Run CLJS tests
cljs-test:
  @npx shadow-cljs compile test


# Check for outdated deps
outdated:
  @clj -M:outdated


# Initialize dev setup:
init:
  npm i
  clojure -A:dev:test -P
  @echo "\n\nReady"
