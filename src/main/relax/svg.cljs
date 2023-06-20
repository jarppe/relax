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
    (doseq [c children]
      (if (sequential? c)
        (append* elem c)
        (append elem c)))
    elem))


(def svg (partial create-element "svg"))
(def g (partial create-element "g"))


(defn circle
  ([cx cy r] (circle nil cx cy r))
  ([attrs cx cy r]
   (create-element "circle" (assoc attrs :cx cx :cy cy :r r))))


(defn path
  ([d] (path nil d))
  ([attrs d]
   (create-element "path" (assoc attrs :d d))))


(defn line
  ([x1 y1 x2 y2] (line nil x1 y1 x2 y2))
  ([attrs x1 y1 x2 y2]
   (create-element "line" (assoc attrs
                                 :x1 x1
                                 :y1 y1
                                 :x2 x2
                                 :y2 y2))))

(defn polyline
  ([points] (polyline nil points))
  ([attrs points]
   (create-element "polyline" (assoc attrs :points (reduce (fn [acc [x y]]
                                                             (str acc x "," y " "))
                                                           ""
                                                           points)))))
