(ns ^:figwheel-load yank.content-script
  (:require [goog.object :as gobj]
            [yank.shared :refer [defaults fetch-options on-storage-change]]
            [cljs.loader :as loader]
            [js.mousetrap]
            [clojure.string :as string])
  (:require-macros [yank.logging :as d]))

;; for extern inference. Better warnings
(set! *warn-on-infer* true)

(def options (atom defaults))

(defn send-message
  "Sends a message using browser runtime
  Gets handled in background script"
  [e]
  (let [^js/browser runtime (gobj/get js/browser "runtime")]
    (.sendMessage runtime #js {:action (:action @options)})))

(defn watcher
  "Watch state atom so that it'll send a message on change"
  [k r old new]
  (let [new-keybind (-> new :keybind :composed)
        old-keybind (-> old :keybind :composed)]
    (when (not= old-keybind new-keybind)
      (.unbind js/Mousetrap old-keybind)
      (.bind js/Mousetrap new-keybind send-message))))

(defn start
  []
  (.bind js/Mousetrap (-> defaults :keybind :composed) send-message)
  (fetch-options options)
  (.addListener ^js/browser (gobj/getValueByKeys js/browser "storage" "onChanged") #(on-storage-change options %))
  (add-watch options :options watcher))

(defonce init (start))

(loader/set-loaded! :content-script)
