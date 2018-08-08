(ns yank.core
  (:require [clojure.edn :as edn]
            [environ.core :refer [env]]))

(def config (edn/read-string "config.edn"))

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
   :icons {"48" "icon.svg"
           "96" "icon.svg"}
   :options-ui {:browser-style true
                :page "options.html"
                :open-in-tab true}
   :background {:persistent true}
   :shadow/outputs

   {:browser-action
    {:init-fn 'yank.browser-action/init}

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
    :default-icon {"16" "icon.svg"
                   "32" "icon.svg"}
    :browser-style true
    :default-popup "browser-action.html"}})

(defn -main
  [& args]
  (let [filename (str "out/" (if (env :release)
                               "manifest-release.edn"
                               "manifest-dev.edn"))
        manifest (if (env :release)
                   manifest
                   (assoc manifest :content-security-policy ["script-src 'self' 'unsafe-eval'; object-src 'self'"]))]
    (spit filename (pr-str manifest))))
