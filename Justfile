set dotenv-load := true
project := "relax"


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


# Reverse proxy from public interface port 8888 to dev http:
proxy:
  @echo "Opening proxy to public interface port 8888. This opens access from interwebs"
  @echo "to your dev http server. Don't leave this on!"
  socat TCP-LISTEN:8888,reuseaddr,fork TCP:127.0.0.1:8900
