(ns defaults.options)

;; define default if nothing is present in storage
(def defaults {:action "org"
               :keybind {:keycode 89
                         :key "y"
                         :alt? false
                         :shift? false
                         :ctrl? true
                         :composed "ctrl+y"}})