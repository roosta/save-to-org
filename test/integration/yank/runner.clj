(ns yank.runner
  (:require [clojure.test :refer [run-tests]]
            [yank.core-test]))

(defn -main
  [& args]
  (run-tests 'yank.core-test))
