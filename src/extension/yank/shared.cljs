(ns yank.shared
  (:require-macros [yank.logging :as d])
  (:require [clojure.walk :as w]))

;; define default if nothing is present in storage
(def defaults {:action "org"
               :keybind {:keycode 89
                         :key "y"
                         :alt? false
                         :meta? false
                         :shift? false
                         :ctrl? true
                         :composed "ctrl+y"}})

(def sync (.. js/browser -storage -sync))
(def runtime (.-runtime js/browser))

(defn save-options
  "save options Takes either an event object and options map or only options"
  ([e opts]
   (.set sync (clj->js {:yank opts}))
   (.preventDefault e))
  ([opts]
   (.set sync (clj->js {:yank opts}))))

(defn on-storage-change
  [ref ^js resp]
  (when-let [new (w/keywordize-keys (js->clj (.. resp -yank -newValue)))]
    (reset! ref new)))

(defn restore-options
  "Get options map and reset state atom with fetched value"
  [ref]
  (let [options-promise (.get sync "yank")]
    (.then options-promise
           (fn [^js resp]
             (if-let [result (w/keywordize-keys (js->clj (.-yank resp)))]
               (reset! ref result)
               (reset! ref defaults)))
           (fn [error]
             (reset! ref defaults)
             (d/error "Failed to restore options, using defaults. Error: " error)))))

(defn fetch-options
  "Handle fetching options, takes an atom as a param"
  [ref]
  (let [sync (.. js/browser -storage -sync)]
    (-> (.get sync "yank")
        (.then (fn [^js resp]
                 (if-let [result (w/keywordize-keys (js->clj (.-yank resp)))]
                   (reset! ref result)))
               (fn [error]
                 (d/log "Failed to get options: " error))))))
