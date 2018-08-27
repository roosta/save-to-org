(ns yank.background
  (:require-macros [yank.logging :as d])
  (:require [yank.shared :refer [fetch-options defaults on-storage-change]]
            [yank.format :as format]))

(def tabs (.-tabs js/browser))
(def runtime (.-runtime js/browser))
(def context-menus (.-contextMenus js/browser))

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
  (-> (execute-script {:code "typeof copyToClipboard === 'function';"})
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
    (-> (load-clipboard-helper tab-id)
        (.then (fn []
                 (execute-script {:code code} tab-id )))
        (.catch (fn [error]
                  (d/error "Failed to copy text: " error))))))

(defn handle-message
  "Handle incoming runtime message, extract info and call copy-as"
  [request ^js sender send-response]
  (when-some [action (.-action request)]
    (let [tab (.-tab sender)
          url (.-url tab)
          tab-id (.-id tab)
          title (.-title tab)
          text (format/as {:action action
                           :url url
                           :title title})]
      (copy-to-clipboard tab-id text))))

(defn handle-click
  []
  (.openOptionsPage runtime))

(defn handle-context
  [^js info tab]
  (let [url (.-linkUrl info)
        tab-id (.-id tab)
        text (.-linkText info)
        action (:action @options)
        text (format/as {:action action
                         :url url
                         :title text})]
    (copy-to-clipboard tab-id text)))

(defn fig-reload
  []
  (.reload runtime))

(defn init
  []
  (create-context-menu)
  (fetch-options options)
  (.addListener (.. js/browser -storage -onChanged) #(on-storage-change options %))
  (.addListener (.. js/browser -browserAction -onClicked) handle-click)
  (.addListener (.. js/browser -contextMenus -onClicked) handle-context)
  (.addListener (.-onMessage runtime) handle-message))
