(ns yank.core-test
  (:require  [cljs.test :as t :include-macros true]
             [yank.background.format :as format]))

(t/deftest format-test
  (t/testing "Testing the various formatting options"
    (t/are [x y] (= x y)

      (format/as {:action "org"
                  :url "https://www.google.com/"
                  :title "[Google]"})
      "[[https://www.google.com/][{Google}]]"

      (format/as {:action "md"
                  :url "https://www.google.com/"
                  :title "[Google]"})
      "[\\[Google\\]](https://www.google.com/)"

      (format/as {:action "textile"
                  :url "https://www.google.com/"
                  :title "\"Google\""})
      "\"\"Google\"\":https://www.google.com/"

      (format/as {:action "asciidoc"
                  :url "https://www.google.com/"
                  :title "[Google]"})
      "https://www.google.com/[[Google\\]]"

      (format/as {:action "rest"
                  :url "https://www.google.com/"
                  :title "`Google_"})
      "`\\`Google\\_ <https://www.google.com/>`_"

      (format/as {:action "html"
                  :url "https://www.google.com/"
                  :title "<Google>&"})
      "<a href=\"https://www.google.com/\">&lt;Google&gt;&amp;</a>"
      (format/as {:action "latex"
                  :url "https://www.google.com/"
                  :title "Google&%$#_{~}^\\"})
      "\\href{https://www.google.com/}{Google\\&\\%\\&\\#\\_\\{\\textasciitilde\\}\\textasciicircum\\textbackslash}"
      )))
