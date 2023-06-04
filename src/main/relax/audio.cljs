(ns relax.audio)


(def audio-urls (->> ["G1.flac" "A1.flac" "B1.flac"
                      "D2.flac" "F2.flac" "G2.flac" "B2.flac"
                      "D3.flac" "E3.flac" "G3.flac" "A3.flac" "B3.flac"
                      "D4.flac" "E4.flac" "F4.flac" "G4.flac" "A4.flac" "B4.flac"
                      "D5.flac" "E5.flac" "F5.flac" "G5.flac" "A5.flac" "B5.flac" "C6.flac"]
                     (mapv (fn [f] (str "audio/nylon-guitar/" f)))))
