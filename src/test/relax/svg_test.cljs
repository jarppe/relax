(ns relax.svg-test
  (:require [clojure.test :as test :refer [deftest testing is]]
            [goog.object :as g]
            [relax.svg :as svg]))


(deftest parse-transform-test
  (testing "parse-transform with nil and empty string"
    (is (= nil
           (svg/parse-transform nil)))
    (is (= {}
           (svg/parse-transform ""))))
  (testing "parse-transform with valid content"
    (is (= {:rotate [1]}
           (svg/parse-transform "rotate(1)")))
    (is (= {:rotate [1 2]}
           (svg/parse-transform "rotate(1 2)")))
    (is (= {:rotate [1 2]
            :scale  [3]}
           (svg/parse-transform "rotate(1 2) scale(3)")))
    (is (= {:rotate [1 2]
            :scale  [3 4]}
           (svg/parse-transform "rotate(1 2) scale(3 4)")))
    (is (= {:rotate    [1 2]
            :scale     [3 4]
            :translate [5 6]
            :skew      [7 8]}
           (svg/parse-transform "rotate(1 2) scale(3 4) translate(5 6) skew(7 8)")))))


(deftest format-transform-test
  (is (= "rotate(1) "
         (svg/format-transform {:rotate [1]})))
  (is (= "translate(4 5 6) rotate(1) scale(2 3) skew(7 8) "
         (svg/format-transform {:rotate    [1]
                                :scale     [2 3]
                                :translate [4 5 6]
                                :skew      [7 8]}))))


(defn make-elem []
  (let [attrs (atom {})
        elem  #js {"setAttributeNS" (fn [_ attr-name attr-value]
                                      (swap! attrs assoc attr-name attr-value))}]
    [elem attrs]))


(deftest merge-special-attrs-test
  (let [[elem] (make-elem)]
    (is (= {:rotate 1}
           (svg/merge-special-attrs elem [[:rotate 1]]))))
  (let [[elem] (make-elem)]
    (is (= {:rotate 1
            :skew   [2 3]}
           (svg/merge-special-attrs elem [[:rotate 1]
                                          [:skew [2 3]]]))))
  (let [[elem] (make-elem)]
    (g/set elem :skew [2 3])
    (is (= {:rotate 1
            :skew   [2 3]}
           (svg/merge-special-attrs elem [[:rotate 1]]))))
  (let [[elem] (make-elem)]
    (g/set elem :skew [2 3])
    (is (= {:translate [1 2]}
           (svg/merge-special-attrs elem [[:rotate 2]
                                          [:transform "translate(1 2)"]]))))
  (let [[elem] (make-elem)]
    (g/set elem :skew [2 3])
    (is (= {:translate [1 2]
            :rotate    2}
           (svg/merge-special-attrs elem [[:transform "translate(1 2)"]
                                          [:rotate 2]])))))


(deftest set-special-attrs-test
  (let [[elem attrs] (make-elem)]
    (svg/set-special-attrs elem [[:rotate 2]])
    (is (= (g/get elem :rotate) 2))
    (is (= {"transform" "rotate(2) "} @attrs)))
  (let [[elem attrs] (make-elem)]
    (g/set elem :skew [1 2])
    (svg/set-special-attrs elem [[:rotate 2]])
    (is (= (g/get elem :skew) [1 2]))
    (is (= (g/get elem :rotate) 2))
    (is (= {"transform" "rotate(2) skew(1 2) "} @attrs)))
  (let [[elem attrs] (make-elem)]
    (g/set elem :skew [1 2])
    (svg/set-special-attrs elem [[:rotate 2]
                                 [:transform "translate(3 4) scale(5)"]])
    (is (= (g/get elem :skew) nil))
    (is (= (g/get elem :rotate) nil))
    (is (= (g/get elem :scale) [5]))
    (is (= {"transform" "translate(3 4) scale(5) "} @attrs))))


(deftest set-attr-test
  (testing "setting simple attr"
    (let [[elem data] (make-elem)]
      (svg/set-attr elem :foo "bar")
      (is (= {"foo" "bar"} @data))))


  (testing "setting multiple simple attrs"
    (let [[elem data] (make-elem)]
      (svg/set-attr elem :foo "foo" :bar "bar" :boz "boz")
      (is (= {"foo" "foo"
              "bar" "bar"
              "boz" "boz"}
             @data))))


  (testing "calling set-attr without attrs is a nop"
    (let [[elem data] (make-elem)]
      (svg/set-attr elem)
      (is (= {}
             @data))))


  (testing "set-attr returns the elem"
    (let [[elem] (make-elem)]
      (is (identical? elem (svg/set-attr elem)))
      (is (identical? elem (svg/set-attr elem :foo "bar")))))


  (testing "set-attr with special attribute"
    (let [[elem attrs] (make-elem)]
      (svg/set-attr elem :foo "hello" :rotate 1)
      (is (= "hello" (get @attrs "foo")))
      (is (= 1.0 (g/get elem :rotate)))
      #_(is (= "rotate(1)" (g/get elem "transform"))))))

