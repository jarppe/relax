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


(defn clear-elem ^js [elem]
  (set-text elem ""))


(defn create-element [tag & children]
  (let [[attrs children] (if (map? (first children))
                           [(first children) (rest children)]
                           [nil children])
        elem             (j/call js/document :createElement tag)]
    (doseq [[attr-name attr-value] attrs]
      (if (= attr-name :class)
        (j/apply-in elem [:classList :add] (map str (if (sequential? attr-value)
                                                      (remove nil? attr-value)
                                                      (cons attr-value nil))))
        (j/call elem :setAttribute (name attr-name) (str attr-value))))
    (doseq [child children]
      (j/call elem :append child))
    elem))


(defn append [elem & children]
  (let [elem (get-elem elem)]
    (doseq [child children]
      (j/call elem :append child))
    elem))


(defn add-class [elem class]
  (let [elem (get-elem elem)]
    (j/call-in elem [:classList :add] class)
    elem))


(defn remove-class [elem class]
  (let [elem (get-elem elem)]
    (j/call-in elem [:classList :remove] class)
    elem))


(defn add-event-listener
  ([elem event handler] (add-event-listener elem event handler nil))
  ([elem event handler opts]
   (let [elem (get-elem elem)]
     (j/call elem :addEventListener (name event) handler opts)
     elem)))
