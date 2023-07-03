(ns relax.svg
  (:require [clojure.string :as str]
            [goog.object :as g]))


(def ^:const xmlns "http://www.w3.org/2000/svg")


(def transform-attr [:translate
                     :rotate
                     :scale
                     :skew])


(def transform-attr? (set transform-attr))
(def special-attr? (conj transform-attr? :transform))


(defn parse-transform [transform]
  (when transform
    (reduce (fn [acc [_ attr args]]
              (assoc acc (keyword attr) (map parse-double (str/split args #"[\s,]"))))
            {}
            (re-seq #"([a-z]+)\(([^)]+)\)\s*" transform))))


(defn format-transform [specials]
  (reduce (fn [acc attr-name]
            (if-let [attr-value (attr-name specials)]
              (str acc
                   (name attr-name)
                   "("
                   (if (sequential? attr-value)
                     (str/join " " attr-value)
                     attr-value)
                   ") ")
              acc))
          ""
          transform-attr))


(defn get-special-attrs [elem]
  (reduce (fn [acc attr-key]
            (if-let [value (g/get elem attr-key)]
              (assoc acc attr-key value)
              acc))
          {}
          transform-attr))


(defn merge-special-attrs [elem specials]
  (reduce (fn [acc [attr-name attr-value]]
            (if (= attr-name :transform)
              (parse-transform attr-value)
              (assoc acc attr-name attr-value)))
          (get-special-attrs elem)
          specials))


(defn set-special-attrs [elem specials]
  (let [attrs (merge-special-attrs elem specials)]
    (doseq [transform-attr-name transform-attr?]
      (g/set elem transform-attr-name (transform-attr-name attrs)))
    (.setAttributeNS elem nil "transform" (format-transform attrs)))
  elem)


(defn set-attr [elem & attrs]
  (when (seq attrs)
    (let [specials (loop [specials                      []
                          [attr-name attr-value & more] attrs]
                     (if-not attr-name
                       specials
                       (let [special? (special-attr? attr-name)]
                         (when-not special?
                           (.setAttributeNS elem nil (name attr-name) (str attr-value)))
                         (recur (if special?
                                  (conj specials [attr-name attr-value])
                                  specials)
                                more))))]
      (when (seq specials)
        (set-special-attrs elem specials))))
  elem)


(defn get-attr [elem attr-name]
  (.getAttributeNS elem  nil (name attr-name)))


(defn append* [elem children]
  (doseq [c children]
    (.appendChild elem c))
  elem)


(defn append [elem & children]
  (append* elem children))


(defn create-element [tag & children]
  (let [elem             (.createElementNS js/document xmlns tag)
        [attrs children] (if (map? (first children))
                           [(first children) (rest children)]
                           [nil children])]
    (g/set elem "translate" [0.0 0.0])
    (g/set elem "rotate" 0.0)
    (g/set elem "scale" 1.0)
    (apply set-attr elem (mapcat identity attrs))
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
