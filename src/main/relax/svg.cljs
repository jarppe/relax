(ns relax.svg
  (:require [applied-science.js-interop :as j]))


(def ^:const xmlns "http://www.w3.org/2000/svg")


(defn set-attr [elem & attrs]
  (loop [[attr-name attr-value & more] attrs]
    (j/call elem :setAttributeNS nil (name attr-name) (str attr-value))
    (if more
      (recur more)
      elem)))


(defn get-attr [elem attr-name]
  (j/call elem :getAttributeNS nil (name attr-name)))



(defn append* [elem children]
  (doseq [c children]
    (j/call elem :appendChild c))
  elem)


(defn append [elem & children]
  (append* elem children))


(defn create-element [tag & children]
  (let [elem             (j/call js/document :createElementNS xmlns tag)
        [attrs children] (if (map? (first children))
                           [(first children) (rest children)]
                           [nil children])]
    (doseq [[attr-name attr-value] attrs]
      (set-attr elem attr-name attr-value))
    (if (sequential? (first children))
      (apply append elem (first children))
      (apply append elem children))))


(def svg (partial create-element "svg"))
(def g (partial create-element "g"))
(def circle (partial create-element "circle"))
(def path (partial create-element "path"))
(def line (partial create-element "line"))
