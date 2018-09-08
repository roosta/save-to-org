(ns yank.format
  (:require [clojure.string :as str]))

(defmulti as
  "Dispatch on :action key, from runtime message, passed from content script"
  (fn [message] (:action message))
  :default "org")

(defmethod as "org"
  ^{:doc "Format URL and title of current tab to org link format"}
  [{:keys [title url]}]
  (let [escape #(str/escape % {\[  "{", \] "}"})]
    (str "[[" url "][" (escape title) "]]")))

(defmethod as "md"
  ^{:doc "Format URL and title of current tab to md link format"}
  [{:keys [title url]}]
  (let [escape #(str/escape % {\[  "\\[", \] "\\]"})]
    (str "[" (escape title) "](" url ")")))

(defmethod as "textile"
  ^{:doc "Format URL and title of current tab to textile link format"}
  [{:keys [title url]}]
  (let [escape #(str/escape % {\"  "\""})]
    (str "\"" (escape title) "\":" url)))

(defmethod as "asciidoc"
  ^{:doc "Format URL and title of current tab to textile link format"}
  [{:keys [title url]}]
  (let [escape #(str/escape % {\] "\\]"})]
    (str url "[" (escape title) "]")))

(defmethod as "rest"
  ^{:doc "Format URL and title of current tab to reStructuredText link format"}
  [{:keys [title url]}]
  (let [escape #(str/escape % {\_ "\\_" \` "\\`"})]
    (str "`" (escape title) " <" url ">`_")))

(defmethod as "html"
  ^{:doc "Format URL and title of current tab to HTML link"}
  [{:keys [title url]}]
  (let [escape #(str/escape % {\< "&lt;", \> "&gt;", \& "&amp;"})]
    (str "<a href=\"" url "\">" (escape title) "</a>")))

(defmethod as "latex"
  ^{:doc "Format URL and title of current tab to LaTeX link"}
  [{:keys [title url]}]
  (let [escape #(str/escape % {\& "\\&", \% "\\%", \$ "\\&" \# "\\#" \_ "\\_"
                               \{ "\\{" \} "\\}" \~ "\\textasciitilde" \^ "\\textasciicircum"
                               \\ "\\textbackslash"})]
    (str "\\href{" url "}" "{" (escape title) "}")))
