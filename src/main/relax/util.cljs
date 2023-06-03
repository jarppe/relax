(ns relax.util)


(defn scaler [[src-min scr-max] [target-min target-max]]
  (let [src-diff    (double (- scr-max src-min))
        target-diff (double (- target-max target-min))]
    (fn [v]
      (+ target-min (* target-diff (/ (- v src-min) src-diff))))))


(defn bound [min-value max-value]
  (fn [v]
    (cond
      (< v min-value) min-value
      (> v max-value) max-value
      :else v)))
