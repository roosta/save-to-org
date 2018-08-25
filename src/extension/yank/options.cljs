(ns yank.options
  (:require [goog.events :as events]
            [yank.shared :refer [defaults sync runtime restore-options save-options]]
            [clojure.string :as string]
            [goog.dom :as dom])
  (:require-macros [yank.logging :as d]))

(def options (atom defaults))

;; mac win android linux
(def os (atom nil))

;; Grab url elements since they are statically defined in html
(def elements {:keybind-input (dom/getElement "keybind-input")
               :format-select (dom/getElement "format-select")
               :form (dom/getElement "options-form")})

(defn get-os
  []
  (let [platform-info (.getPlatformInfo runtime)]
    (.then platform-info
           (fn [^js resp]
             (reset! os (.-os resp)))
           (fn [error]
             (d/error "Failed to get os from runtime. Error: " error)))))

(defn handle-keydown
  "handle valid keybinds and reset state atom"
  [e]
  (let [keycode (.-keyCode e)
        key (re-matches #"^[a-z1-9]" (string/lower-case (.fromCharCode js/String keycode)))
        alt? (.-altKey e)
        shift? (.-shiftKey e)
        meta? (.-metaKey e)
        ctrl? (.-ctrlKey e)]
    (.preventDefault e)
    (when key
      (let [alt (when alt? (if (not= @os "mac")
                             "alt"
                             "option"))
            ctrl (when ctrl? "ctrl")
            shift (when shift? "shift")
            meta (when meta? (if (not= @os "mac")
                               "meta"
                               "command"))
            raw (remove string/blank? [alt ctrl shift meta key])
            composed (string/join "+" raw)]
        (swap! options assoc :keybind {:keycode keycode
                                       :key key
                                       :meta? meta?
                                       :alt? alt?
                                       :shift? shift?
                                       :ctrl? ctrl?
                                       :composed composed})))))

(defn input-sync
  "Keep input field up to date with options atom"
  [k r old new]
  (set! (.-value (:format-select elements)) (:action new))
  (set! (.-value (:keybind-input elements)) (-> new :keybind :composed)))

(defn handle-reset
  "Reset value in state and input field"
  [e]
  (.preventDefault e)
  (reset! options defaults)
  (save-options defaults))

(defn handle-format-change
  "set options :action field on <select> change"
  [e]
  (let [value (.. e -target -value)]
    (swap! options assoc :action value)))

(defn fig-reload
  []
  (let [runtime (.-runtime js/browser)]
    (.reload runtime)))

(defn init
  []
  (add-watch options :input-sync input-sync)
  (restore-options options)
  (get-os)
  (events/listen (:keybind-input elements) "keydown" handle-keydown)
  (events/listen (:format-select elements) "change" handle-format-change)
  (events/listen (:form elements) "reset" handle-reset)
  (events/listen (:form elements) "submit" #(save-options % @options)))
