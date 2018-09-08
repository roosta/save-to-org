(ns yank.core
  (:require [hiccup.page :refer [include-js include-css html5 doctype]]
            [environ.core :refer [env]]
            [clojure.string :as str]))

(def formats [{:value "org" :text "Org-mode"}
              {:value "md" :text "Markdown"}
              {:value "textile" :text "Textile"}
              {:value "asciidoc" :text "AsciiDoc"}
              {:value "rest" :text "reStructuredText"}
              {:value "html" :text "HTML"}
              {:value "latex" :text "LaTeX"}])

(defn head
  ([{:keys [component title]}]
   [:head
    [:meta {:charset "utf-8"}]
    (when title [:title title])
    (include-css (str "/css/" component ".css"))])
  ([] [:head
       [:meta {:charset "utf-8"}]]))

(defn browser-action-body
  []
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

   [(include-js "js/cljs_base.js")
    (include-js "js/browser-action.js")]))

(defn options-body
  []
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
   [(include-js "js/cljs_base.js")
    (include-js "js/options.js")]))

(defn options-html
  []
  (html5
   (head {:component "options"
          :title "Yank extension options page"})
   (options-body)))

(defn browser-action-html
  []
  (html5
   (head {:component "browser-action"})
   (browser-action-body)))

(defn -main
  [& args]
    (let [filename (str "resources/" (if (env :release) "release" "dev") "/options.html")]
      (spit filename (options-html))
      (println (str "Wrote: " filename)))
    (let [filename (str "resources/" (if (env :release) "release" "dev") "/browser-action.html")]
      (spit filename (browser-action-html))
      (println (str "Wrote: " filename))))
