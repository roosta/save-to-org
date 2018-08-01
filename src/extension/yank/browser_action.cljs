(ns yank.browser-action
  (:require-macros [yank.logging :as d])
  (:require [yank.shared :refer [defaults sync runtime restore-options save-options]]
            [goog.object :as gobj]
            [goog.events :as events]
            [goog.dom :as dom]))

(def options (atom defaults))

(def elements {:format-select (dom/getElement "format-select")
               :form (dom/getElement "options-form")})

(defn input-sync
  "Keep input field up to date with options atom"
  [k r old new]
  (gobj/set (:format-select elements) "value" (:action new)))

(defn handle-format-change
  "set options :action field on <select> change"
  [e]
  (let [value (gobj/getValueByKeys e "target" "value")]
    (swap! options assoc :action value)
    (save-options @options)))

(defn init
  []
  (add-watch options :input-sync input-sync)
  (restore-options options)
  (events/listen (:format-select elements) "change" handle-format-change))
