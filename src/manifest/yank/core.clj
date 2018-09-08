(ns yank.core
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [environ.core :refer [env]])
  (:gen-class))

(def project (-> (System/getProperty "user.dir")
                 (str "/project.clj")
                 slurp
                 read-string))

(def version (nth project 2))
(def description (nth project 4))
(def url (nth project 6))

(def manifest {:manifest_version 2
               :name "yank"
               :version version
               :description description
               :homepage_url url

               :icons {"48" "icons/icon-dark.svg"
                       "96" "icons/icon-dark.svg"}

               :permissions ["tabs"
                             "clipboardWrite"
                             "contextMenus"
                             "storage"
                             "<all_urls>"]

               :options_ui {:page "options.html"
                            :browser_style true
                            :open_in_tab true}

               :content_scripts [{:matches ["http://*/*"
                                            "https://*/*"]
                                  :js ["js/cljs_base.js"
                                       "js/content-script.js"]
                                  :all_frames true
                                  :run_at "document_end"}]

               :browser_action {:default_title "Yank format"
                                :browser_style true
                                :default_icon {"16" "icons/icon-light.svg"
                                               "32" "icons/icon-light.svg"}
                                :theme_icons [{:light "icons/icon-light.svg"
                                               :dark "icons/icon-dark.svg"
                                               :size 16}
                                              {:light "icons/icon-light.svg"
                                               :dark "icons/icon-dark.svg"
                                               :size 32}]
                                :default_popup "popup.html"}

               :applications {:gecko {:id "yank@roosta.sh"}}

               :background {:scripts ["js/cljs_base.js"
                                      "js/background.js"]}})

(def manifest-dev (-> manifest
                      (assoc  :content_security_policy "script-src 'self' 'unsafe-eval'; object-src 'self'")))

(defn compile-json
  [{:keys [escape-slash dev?]}]
   (json/write-str (if dev? manifest-dev manifest) :escape-slash escape-slash))

(defn -main
  [& args]
  (let [dev? (= (env :location) "dev")
        path (str "resources/" (env :location) "/manifest.json")
        out (compile-json {:escape-slash false :dev? dev?})]
    (spit path out)
    (println (str "Wrote: " path))))
