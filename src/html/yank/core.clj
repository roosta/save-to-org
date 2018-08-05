(ns yank.core
  (:require [hiccup.page :refer [include-js include-css html5 doctype]]
            [environ.core :refer [env]]
            [clojure.string :as str])
  (:gen-class))

(def formats [{:value "org" :text "Org-mode"}
              {:value "md" :text "Markdown"}
              {:value "textile" :text "Textile"}
              {:value "asciidoc" :text "AsciiDoc"}
              {:value "rest" :text "reStructuredText"}
              {:value "html" :text "HTML"}
              {:value "latex" :text "LaTeX"}])

(defn head
  ([{:keys [component title css-ext]}]
   [:head
    [:meta {:charset "utf-8"}]
    (when title [:title title])
    (include-css (str "/css/" component css-ext))])
  ([] [:head
       [:meta {:charset "utf-8"}]]))

(defn browser-action-body
  [dev?]
  (into
   [:body

    [:h3
     [:span {:class "bracket" :id "left-backet"} "["]
     "Yank format"
     [:span {:class "bracket" :id "right-bracket"} "]"]]

    ;; [:p#error-message]

    [:select {:class "input" :id "format-select"}
     (for [f formats]
       [:option {:value (:value f)} (:text f)])]]

     [(include-js "out/browser-action.js")]))

(defn options-body
  [dev?]
  (into
   [:body
    [:form {:id "options-form"}

     [:h3
      [:span {:class "bracket" :id "left-backet"} "["]
      "Yank options"
      [:span {:class "bracket" :id "right-bracket"} "]"]]

     [:div {:class "block" :id "keybinding"}
      [:label {:class "label"} "Keybinding"]
      [:input {:class "input" :type "text" :id "keybind-input"}]]

     [:div {:class "block" :id "format"}
      [:label {:class "label"} "Markup format"]
      [:select {:class "input" :id "format-select"}
       (for [f formats]
         [:option {:value (:value f)} (:text f)])]]

     [:div {:class "block" :id "buttons"}
      [:button {:type "reset"}
       "Reset"]
      [:button {:type "submit"}
       "Save"]]]]
     [(include-js "out/options.js")]))

(defn options-html
  [dev? css-ext]
  (html5
   (head {:component "options"
          :css-ext css-ext
          :title "Yank extension options page"})
   (options-body dev?)))

(defn browser-action-html
  [dev? css-ext]
  (html5
   (head {:component "browser-action"
          :css-ext css-ext})
   (browser-action-body dev?)))

(defn -main
  [& args]
  (let [dev? (= (env :location) "dev")
        css-ext (env :css-ext)
        options-path (str "out/" (env :location) "/options.html")
        browser-action-path (str "out/" (env :location) "/browser-action.html")]
    (spit options-path (options-html dev? css-ext))
    (println (str "Wrote: " options-path))
    (spit browser-action-path (browser-action-html dev? css-ext))
    (println (str "Wrote: " browser-action-path))))
