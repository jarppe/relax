(ns web
  (:require [clojure.string :as str]
            [applied-science.js-interop :as j]))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (println "user.cljs: init"))


(comment


  (def note (->> ["C" "D" "E" "F" "G" "A" "B"]
                 (map-indexed (fn [n note] [note n]))
                 (into {})))

  (->> (str/split "G5.flac G4.flac G3.flac G2.flac G1.flac 
                   F5.flac F4.flac F2.flac E5.flac E4.flac 
                   E3.flac D5.flac D4.flac D3.flac D2.flac 
                   C6.flac B5.flac B4.flac B3.flac B2.flac 
                   B1.flac A5.flac A4.flac A3.flac A1.flac"
                  #"\s+")
       (map (fn [f]
              (let [[_ n o] (re-matches #"([A-Z])(\d)\.flac" f)]
                [(js/parseInt o) (note n) f])))
       (sort)
       (mapv (fn [[_ _ f]] f))
       #_#_#_(filter (fn [s] (re-find #"_ff\." s)))
           (sort)
         (vec))
  ;; => 
  ;; [["C3" ["C3_ff.flac" "C3_pp.flac"]]
  ;;  ["C4" ["C4_pp.flac" "C4_ff.flac"]]
  ;;  ["C5" ["C5_pp.flac" "C5_ff.flac"]]
  ;;  ["C6" ["C6_pp.flac" "C6_ff.flac"]]
  ;;  ["C7" ["C7_pp.flac" "C7_ff.flac"]]
  ;;  ["G3" ["G3_pp.flac" "G3_ff.flac"]]
  ;;  ["G4" ["G4_pp.flac" "G4_ff.flac"]]
  ;;  ["G5" ["G5_ff.flac" "G5_pp.flac"]]
  ;;  ["G6" ["G6_pp.flac" "G6_ff.flac"]]]

  (->> (str/split "G5.flac G4.flac G3.flac G2.flac G1.flac 
                   F5.flac F4.flac F2.flac E5.flac E4.flac 
                   E3.flac D5.flac D4.flac D3.flac D2.flac 
                   C6.flac B5.flac B4.flac B3.flac B2.flac 
                   B1.flac A5.flac A4.flac A3.flac A1.flac"
                  #"\s+")
       (map (fn [f] (str "<link rel=\"preload\" href=\"/audio/nylon-guitar/" f "\" as=\"audio\" type=\"audio/flac\">")))
       (map println))
  ;; => 
  ["<link rel=\"preload\" href=\"/audio/nylon-guitar/G5.flac\" as=\"audio\" type=\"audio/flac\">"
   "<link rel=\"preload\" href=\"/audio/nylon-guitar/G4.flac\" as=\"audio\" type=\"audio/flac\">"
   "<link rel=\"preload\" href=\"/audio/nylon-guitar/G3.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/G2.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/G1.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/F5.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/F4.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/F2.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/E5.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/E4.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/E3.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/D5.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/D4.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/D3.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/D2.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/C6.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/B5.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/B4.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/B3.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/B2.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/B1.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/A5.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/A4.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/A3.flac\" as=\"audio\" type=\"audio/flac\" >" "<link rel=\"preload\" href=\"/audio/nylon-guitar/A1.flac\" as=\"audio\" type=\"audio/flac\" >"]





  ;
  )
