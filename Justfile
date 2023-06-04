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


# Make icons:
make-icons:
  inkscape -w 512 -h 512  ./public/relax.svg  -o ./public/relax.512.png
  inkscape -w 256 -h 256  ./public/relax.svg  -o ./public/relax.256.png
  inkscape -w 128 -h 128  ./public/relax.svg  -o ./public/relax.128.png
  inkscape -w 64  -h 64   ./public/relax.svg  -o ./public/relax.64.png
  inkscape -w 32  -h 32   ./public/relax.svg  -o ./public/relax.32.png
