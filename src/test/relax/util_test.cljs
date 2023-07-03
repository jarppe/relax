(ns relax.util-test
  (:require [clojure.test :as test :refer [deftest testing is]]
            [goog.object :as g]
            [relax.util :as u :refer [js-get js-get-in js-set]]))


(deftest js-get-test
  (let [elem #js {"foo" 42}]
    (is (= 42 (js-get elem "foo"))))
  (let [elem #js {":foo" 42}]
    (is (= 42 (js-get elem :foo)))))


(deftest js-get-in-test
  (let [elem #js {"foo" #js {"bar" #js {"boz" 42}}}]
    (is (= 42 (js-get-in elem ["foo" "bar" "boz"]))))
  (let [elem #js {"foo" #js {"bar" #js {"boz" 42}}}]
    (is (= nil (js-get-in elem ["foo" "barz" "boz"])))))

(deftest js-set-test
  (let [elem #js {}]
    (js-set elem "foo" 42)
    (is (= 42 (js-get elem "foo"))))
  (let [elem #js {}]
    (js-set elem :foo 42)
    (is (= 42 (js-get elem :foo))))
  (let [elem #js {}]
    (js-set elem :foo 42 :bar 1337 :boz "hello")
    (is (= [42 1337 "hello"] (map (partial js-get elem) [:foo :bar :boz])))))
