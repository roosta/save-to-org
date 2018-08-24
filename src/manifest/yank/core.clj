(ns yank.core
  (:require [aero.core :refer (read-config)]
            [environ.core :refer [env]]))

(def config (read-config "config.edn"))

(def manifest
  {:name "yank"
   :version (:version config)
   :description "Yank current page URL to clipboard as various markup formats"
   :manifest-version 2
   :homepage-url "https://github.com/roosta/yank"
   :permissions ["tabs"
                 "clipboardWrite"
                 "contextMenus"
                 "storage"
                 "<all_urls>"]
   :applications {:gecko {:id "yank@roosta.sh"}}
   :icons {"48" "icon-dark.svg"
           "96" "icon-dark.svg"}
   :options-ui {:browser-style true
                :page "options.html"
                :open-in-tab true}
   :background {:persistent true}
   :shadow/outputs

   {:popup
    {:output-type :chrome/single-file
     :init-fn 'yank.browser-action/init}

    :content-script
    {:init-fn 'yank.content-script/init
     :chrome/options {:matches ["http://*/*"
                                "https://*/*"]
                      :run-at "document_end"
                      :all_frames true}}

    :options
    {:init-fn 'yank.options/init}

    :background
    {:init-fn 'yank.background/init}}

   :browser-action
   {:default-title "Yank format"
    :default-icon {"16" "icon-light.svg"
                   "32" "icon-light.svg"}
    :theme-icons [{:light "icon-light.svg"
                   :dark "icon-dark.svg"
                   :size 16}
                  {:light "icon-light.svg"
                   :dark "icon-dark.svg"
                   :size 32}]
    :browser-style true
    :default-popup "browser-action.html"}})

(defn -main
  [& args]
  (let [filename (str "out/manifest.edn" )
        manifest (if (env :release)
                   manifest
                   (assoc manifest :content-security-policy ["script-src 'self' 'unsafe-eval'; object-src 'self'"]))]
    (spit filename (pr-str manifest))
    (println (str "Wrote " filename))))
