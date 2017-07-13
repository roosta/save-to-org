(ns save-to-org.options.core
  (:require [goog.events :as events]
            [goog.object :as gobj]
            [clojure.walk :as w]
            [clojure.string :as string]
            [goog.dom :as dom])
  (:require-macros [utils.logging :as d]))

(def keybind (atom {:code 432
                    :key "R"
                    :ctrl? true
                    :alt? false
                    :shift false
                    :human "ctrl+r"}))

(defn save-options!
  [e]
  (let [sync (gobj/getValueByKeys js/browser "storage" "sync")
        el (dom/getElement "keybind-input")
        value (gobj/get el "value")]
    (.set sync (clj->js {:keybind-opt @keybind})))
  (.preventDefault e))

(defn restore-options!
  []
  (let [el (dom/getElement "keybind-input")
        sync (.. js/browser -storage -sync)]
    (-> (.get sync "keybind-opt")
        (.then (fn [resp]
                 (when-let [value (js->clj (gobj/get resp "keybind-opt"))]
                   (let [str->keyword (w/keywordize-keys value)]
                     (gobj/set el "value" (:human str->keyword))
                     (reset! keybind str->keyword))))
               (fn [error]
                 (d/log error))))))

(defn handle-keydown
  [e]
  (let [value (.. e -target -value)
        non-values #{16 17 18}
        field(dom/getElement "keybind-input")
        keycode (.-keyCode e)
        key (when  (not (contains? non-values keycode))
              (.fromCharCode js/String keycode))
        alt? (.-altKey e)
        shift? (.-shiftKey e)
        ctrl? (.-ctrlKey e)
        raw-human (remove string/blank? [(when alt? "Alt") (when ctrl? "Ctrl") (when shift? "Shift") key])
        human (string/join "+" raw-human)]
    (.preventDefault e)
    (gobj/set field "value" human)
    (reset! keybind {:keycode keycode
                     :key key
                     :alt? alt?
                     :shift? shift?
                     :ctrl? ctrl?
                     :human human})))

(defn init!
  []
  (d/log "opts init!")
  (let [form (dom/getElement "keybind-form")
        field (dom/getElement "keybind-input")]
    (restore-options!)
    (events/listen form (.-KEYDOWN events/EventType) handle-keydown)
    (events/listen form (.-SUBMIT events/EventType) save-options!)))
