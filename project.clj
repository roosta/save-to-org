(defproject yank "1.0.3"
  :description "Yank current page URL to clipboard as various markup formats"
  :url "https://github.com/roosta/yank"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [environ "1.1.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojurescript "1.10.339"]]

  :clean-targets ^{:protect false} ["target"
                                    "resources/dev/js"
                                    "resources/dev/css"
                                    "resources/release/css"
                                    "resources/release/js"
                                    "resources/release/background.html"
                                    "resources/release/options.html"
                                    "resources/release/popup.html"
                                    "resources/dev/background.html"
                                    "resources/dev/options.html"
                                    "resources/dev/popup.html"
                                    "resources/release/manifest.json"
                                    "resources/dev/manifest.json"]

  :source-paths ["src/lib"]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.4.4"]
            [lein-environ "1.1.0"]
            [lein-doo "0.1.10"]
            [lein-garden "0.3.0"]
            [lein-shell "0.5.0"]]

  :minify-assets [[:css {:source "resources/dev/css/options.css"
                         :target "resources/release/css/options.min.css"}]
                  [:css {:source "resources/dev/css/popup.css"
                         :target "resources/release/css/popup.min.css"}]]

  :aliases {"release" ["do"
                       ["clean"]
                       ["cljsbuild" "once" "background-release" "options-release" "content-script-release" "popup-release"]
                       ["with-profile" "+html-release" "run"]
                       ["with-profile" "+manifest-release" "run"]
                       ["garden" "once" "options"]
                       ["garden" "once" "popup"]
                       ["minify-assets"]
                       ["shell" "rm" "-r"
                        "resources/release/js/background"
                        "resources/release/js/popup"
                        "resources/release/js/options"
                        "resources/release/js/content_script"]
                       ["shell" "script/package.sh"]]


            "build-all" ["do"
                         ["garden" "once" "options"]
                         ["garden" "once" "popup"]
                         ["with-profile" "+html" "run"]
                         ["with-profile" "+manifest" "run"]
                         ["cljsbuild" "once" "background" "options" "content-script" "popup"]]

            "build" ["do"
                     ["garden" "once" "options"]
                     ["garden" "once" "popup"]
                     ["with-profile" "+html" "run"]
                     ["with-profile" "+manifest" "run"]]

            "package"  ["shell" "script/package.sh"]

            "content"  ["cljsbuild" "auto" "content-script"]

            "css"      ["do"
                        ["garden" "once" "popup"]
                        ["garden" "once" "options"]]

            "html"     ["with-profile" "+html" "run"]

            "html-test" ["with-profile" "+html" "test"]

            "manifest" ["with-profile" "+manifest" "run"]

            "manifest-test" ["with-profile" "+manifest" "test"]

            "integration-test" ["with-profile" "+integration-test" "test"]

            "background-test" ["doo" "phantom" "background-test" "once"]

            "test-all" ["do"
                        ["release"]
                        ["with-profile" "+html" "test"]
                        ["with-profile" "+manifest" "test"]
                        ["doo" "phantom" "background-test" "once"]
                        ["with-profile" "+integration-test" "test"]]

            "fig"      ["figwheel" "background" "options" "popup"]}

  :garden {:builds [{:id "popup"
                     :source-paths ["src/styles/popup"]
                     :stylesheet yank.core/screen
                     :compiler {:output-to "resources/dev/css/popup.css"
                                :pretty-print? true}}

                    {:id "options"
                     :source-paths ["src/styles/options"]
                     :stylesheet yank.core/screen
                     :compiler {:output-to "resources/dev/css/options.css"
                                :pretty-print? true}}]}

  :figwheel {:server-port 6888
             :css-dirs ["resources/dev/css"]
             :repl        true}

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.17-SNAPSHOT"]
                                  [hickory "0.7.1"]
                                  [cider/piggieback "0.3.6"]]
                   :plugins      [[lein-figwheel "0.5.17-SNAPSHOT"]]

                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}

                   :source-paths ["script" "dev"]}

             :html {:env {:location "dev"
                          :css-ext ".css"}
                    :main "yank.core"
                    :source-paths ["src/html" "test/html"]}

             :html-release [:html {:env {:location "release"
                                         :css-ext ".min.css"}}]

             :manifest {:main "yank.core"
                        :env {:location "dev"}
                        :source-paths ["src/manifest" "test/manifest"]}

             :manifest-release [:manifest {:env {:location "release"}}]

             :integration-test {:dependencies [[etaoin "0.2.8-SNAPSHOT"]]
                                :source-paths ["test/integration"]}}

  :cljsbuild {:builds [{:id           "background"
                        :source-paths ["src/background"]
                        :figwheel     {:debug true
                                       :on-jsload "yank.background.core/fig-reload"}
                        :compiler     {:main          yank.background
                                       :asset-path    "js/background"
                                       :output-to     "resources/dev/js/background/main.js"
                                       :output-dir    "resources/dev/js/background"
                                       :infer-externs true
                                       :preloads [figwheel.preload]
                                       :source-map    true
                                       :optimizations :none}}

                       {:id           "background-test"
                        :source-paths ["src/background" "test/background"]
                        :compiler     {:output-to     "target/cljs/background/test.js"
                                       :main yank.runner
                                       :optimizations :none}}

                       {:id           "options"
                        :source-paths ["src/options"]
                        :figwheel     {:on-jsload "yank.options.core/fig-reload"
                                       :debug true}
                        :compiler     {:main          yank.options
                                       :asset-path    "js/options"
                                       :output-to     "resources/dev/js/options/main.js"
                                       :output-dir    "resources/dev/js/options"
                                       :preloads [figwheel.preload]
                                       :infer-externs true
                                       :source-map    true
                                       :optimizations :none}}

                       {:id           "popup"
                        :source-paths ["src/popup"]
                        :figwheel     {:on-jsload "yank.popup.core/fig-reload"
                                       :debug true}
                        :compiler     {:main          yank.popup
                                       :asset-path    "js/popup"
                                       :output-to     "resources/dev/js/popup/main.js"
                                       :preloads [figwheel.preload]
                                       :output-dir    "resources/dev/js/popup"
                                       :infer-externs true
                                       ;; :source-map    true
                                       :optimizations :none}}

                       {:id           "content-script"
                        :source-paths ["src/content_script"]
                        :compiler     {:main          yank.content-script
                                       :asset-path    "js/content_script"
                                       :output-to     "resources/dev/js/content_script/main.js"
                                       :output-dir    "resources/dev/js/content_script"
                                       :infer-externs true
                                       :foreign-libs  [{:file     "src/js/mousetrap.js"
                                                        :file-min "src/js/mousetrap.min.js"
                                                        :provides ["js.mousetrap"]}]
                                       :pseudo-names  true
                                       :pretty-print  true
                                       :optimizations :advanced}}

                       {:id           "background-release"
                        :source-paths ["src/background"]
                        :compiler     {:main          yank.background
                                       :asset-path    "js/background"
                                       :output-to     "resources/release/js/background.js"
                                       :output-dir    "resources/release/js/background"
                                       :infer-externs true
                                       :elide-asserts true
                                       :optimizations :advanced}}

                       {:id           "options-release"
                        :source-paths ["src/options"]
                        :compiler     {:main          yank.options
                                       :asset-path    "js/options"
                                       :output-to     "resources/release/js/options.js"
                                       :output-dir    "resources/release/js/options"
                                       :infer-externs true
                                       :elide-asserts true
                                       :optimizations :advanced}}

                       {:id           "content-script-release"
                        :source-paths ["src/content_script"]
                        :compiler     {:main          yank.content-script
                                       :asset-path    "js/content-script"
                                       :output-to     "resources/release/js/content_script.js"
                                       :output-dir    "resources/release/js/content_script"
                                       :elide-asserts true
                                       :infer-externs true
                                       :foreign-libs  [{:file     "src/js/mousetrap.js"
                                                        :file-min "src/js/mousetrap.min.js"
                                                        :provides ["js.mousetrap"]}]
                                       :optimizations :advanced}}

                       {:id           "popup-release"
                        :source-paths ["src/popup"]
                        :compiler     {:main          yank.popup
                                       :asset-path    "js/popup"
                                       :output-to     "resources/release/js/popup.js"
                                       :output-dir    "resources/release/js/popup"
                                       :elide-asserts true
                                       :infer-externs true
                                       :optimizations :advanced}}]})
