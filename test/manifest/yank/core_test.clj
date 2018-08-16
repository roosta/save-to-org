(ns yank.core-test
  (:require [yank.core :as core]
            [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]
            [clojure.test :as t :refer [deftest testing is are]]))

(deftest compile-json
  (let [dev          (json/read-str (core/compile-json {:escape-slash true :dev? true}) :key-fn keyword)
        release      (json/read-str (core/compile-json {:escape-slash true :dev? false}) :key-fn keyword)
        checking     {:version         "1.0.3"
                      :description     "Yank current page URL to clipboard as various markup formats"
                      :homepage_url    "https://github.com/roosta/yank"
                      :permissions     ["tabs"
                                        "clipboardWrite"
                                        "contextMenus"
                                        "storage"
                                        "<all_urls>"]
                      :content_scripts [{:matches    ["http://*/*"
                                                      "https://*/*"]
                                         :js         ["js/content_script.js"]
                                         :all_frames true
                                         :run_at     "document_end"}]}
        checking-dev (-> checking
                         (assoc  :content_security_policy "script-src 'self' 'unsafe-eval'; object-src 'self'")
                         (assoc-in [:content_scripts 0 :js] ["js/content_script/main.js"]))]

    (testing "Testing read/write JSON manifest: dev"
      (are [x y] (= x y)
        (:version dev)         (:version checking-dev)
        (:description dev)     (:description checking-dev)
        (:homepage_url dev)    (:homepage_url checking-dev)
        (:permissions dev)     (:permissions checking-dev)
        (:content_scripts dev) (:content_scripts checking-dev)))

    (testing "Testing read/write JSON manifest: release"
      (are [x y] (= x y)
        (:version release)         (:version checking)
        (:description release)     (:description checking)
        (:homepage_url release)    (:homepage_url checking)
        (:permissions release)     (:permissions checking)))))
