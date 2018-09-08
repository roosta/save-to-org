(ns ^:figwheel-load yank.background
  (:require-macros [yank.logging :as d])
  (:require [goog.object :as gobj]
            [cljs.loader :as loader]
            [yank.shared :refer [fetch-options defaults on-storage-change]]
            [yank.format :as format]))

;; for extern inference. Better warnings
(set! *warn-on-infer* true)

(def ^js/browser tabs (gobj/get js/browser "tabs"))
(def ^js/browser runtime (gobj/get js/browser "runtime"))
(def ^js/browser context-menus (gobj/get js/browser "contextMenus"))

(def options (atom defaults))

(defn create-context-menu
  []
  (.create context-menus (clj->js {:id "yank-link"
                                   :title "Yank link to clipboard"
                                   :contexts ["link"]})))
(defn execute-script
  "Execute a script using js/browser.tabs
  'obj' param is a javascript object conforming to this:
  https://developer.mozilla.org/en-US/Add-ons/WebExtensions/API/tabs/executeScript
  optionally takes a tab-id to select which tab to execute script inside
  Returns a js/Promise"
  ([obj]
   (.executeScript tabs (clj->js obj)))
  ([obj tab-id]
   (.executeScript tabs tab-id (clj->js obj))))

(defn load-clipboard-helper
  "load js function defined in clipboard-helper.js"
  [tab-id]
  (-> ^js/Promise (execute-script {:code "typeof copyToClipboard === 'function';"})
      (.then (fn [result]
               (when (or  (not result) (false? (first result)))
                 (execute-script {:file "clipboard-helper.js"} tab-id))))
      (.catch (fn [error]
                (d/error "Failed to load clipboard helper: " error)))))

(defn copy-to-clipboard
  "Copy text to clipboard by injecting the formatted input (text)
  as an argment to the loaded 'copyToClipboard"
  [tab-id text]
  (let [code (str "copyToClipboard(" (.stringify js/JSON text) ");")]
    (-> ^js/Promise (load-clipboard-helper tab-id)
        (.then (fn []
                 (execute-script {:code code} tab-id )))
        (.catch (fn [error]
                  (d/error "Failed to copy text: " error))))))

(defn handle-message
  "Handle incoming runtime message, extract info and call copy-as"
  [request sender send-response]
  (when-some [action (gobj/get request "action")]
    (let [tab (gobj/get sender "tab")
          url (gobj/get tab "url")
          tab-id (gobj/get tab "id")
          title (gobj/get tab "title")
          text (format/as {:action action
                           :url url
                           :title title})]
      (copy-to-clipboard tab-id text))))

(defn handle-click
  []
  (.openOptionsPage runtime))

(defn handle-context
  [info tab]
  (let [url (gobj/get info "linkUrl")
        tab-id (gobj/get tab "id")
        text (gobj/get info "linkText")
        action (:action @options)
        text (format/as {:action action
                         :url url
                         :title text})]
    (copy-to-clipboard tab-id text)))

(defn start
  []
  (create-context-menu)
  (fetch-options)
  (.addListener ^js/browser (gobj/getValueByKeys js/browser "storage" "onChanged") #(on-storage-change options %))
  (.addListener ^js/browser (gobj/getValueByKeys js/browser "browserAction" "onClicked") handle-click)
  (.addListener ^js/browser (gobj/getValueByKeys js/browser "contextMenus" "onClicked") handle-context)
  (.addListener ^js/browser (gobj/get runtime "onMessage") handle-message))

(defonce init (start))

(loader/set-loaded! :background)
