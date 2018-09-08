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

(defn popup-body
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

   (if dev?
     [(include-js "js/popup/goog/base.js")
      (include-js "setup.js")
      (include-js "js/popup/cljs_deps.js")
      (include-js "popup.js")]
     [(include-js "js/popup.js")])))

(defn background-body
  [dev?]
  (into
   [:body]
   (if dev?
     [(include-js "js/background/goog/base.js")
      (include-js "js/background/goog/deps.js")
      (include-js "js/background/cljs_deps.js")
      (include-js "setup.js")
      (include-js "background.js")]
     [(include-js "js/background.js")])))

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

   (if dev?
     [(include-js "js/options/goog/base.js")
      (include-js "setup.js")
      (include-js "js/options/cljs_deps.js")
      (include-js "options.js")]
     [(include-js "js/options.js")])))

(defn options-html
  [dev? css-ext]
  (html5
   (head {:component "options"
          :css-ext css-ext
          :title "Yank extension options page"})
   (options-body dev?)))

(defn popup-html
  [dev? css-ext]
  (html5
   (head {:component "popup"
          :css-ext css-ext})
   (popup-body dev?)))

(defn background-html
  [dev?]
  (html5
   (head)
   (background-body dev?)))

(defn -main
  [& args]
  (let [dev? (= (env :location) "dev")
        css-ext (env :css-ext)
        options-path (str "resources/" (env :location) "/options.html")
        popup-path (str "resources/" (env :location) "/popup.html")
        background-path (str "resources/" (env :location) "/background.html")]
    (spit options-path (options-html dev? css-ext))
    (println (str "Wrote: " options-path))
    (spit popup-path (popup-html dev? css-ext))
    (println (str "Wrote: " popup-path))
    (spit background-path (background-html dev?))
    (println (str "Wrote: " background-path))))
