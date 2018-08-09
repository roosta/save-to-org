(ns yank.content-script
  (:require [yank.shared :refer [defaults fetch-options on-storage-change]]
            ["mousetrap" :as mousetrap]
            [clojure.string :as string])
  (:require-macros [yank.logging :as d]))

(def options (atom defaults))

(defn send-message
  "Sends a message using browser runtime
  Gets handled in background script"
  [e]
  (let [runtime (.-runtime js/browser)]
    (.sendMessage runtime #js {:action (:action @options)})))

(defn watcher
  "Watch state atom so that it'll send a message on change"
  [k r old new]
  (let [new-keybind (-> new :keybind :composed)
        old-keybind (-> old :keybind :composed)]
    (when (not= old-keybind new-keybind)
      (mousetrap/unbind old-keybind)
      (mousetrap/bind new-keybind send-message))))

(defn init
  []
  (mousetrap/bind (-> defaults :keybind :composed) send-message)
  (fetch-options options)
  (.addListener (.. js/browser -storage -onChanged) #(on-storage-change options %))
  (add-watch options :options watcher))
