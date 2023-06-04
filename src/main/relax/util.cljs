(ns relax.util
  (:require [applied-science.js-interop :as j]))


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


(defn get-elem ^js [id]
  (if (string? id)
    (j/call js/document :getElementById id)
    id))


(defn set-attr ^js [elem & attrs]
  (let [elem (get-elem elem)]
    (loop [[attr-name attr-value & more] attrs]
      (j/call elem :setAttribute (name attr-name) (str attr-value))
      (if (seq more)
        (recur more)
        elem))))


(defn set-text ^js [elem text-content]
  (let [elem (get-elem elem)]
    (j/assoc! elem :textContent text-content)
    elem))
