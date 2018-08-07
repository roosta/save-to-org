(ns yank.core
  (:require [garden.def :refer [defstylesheet defstyles]]
            [garden.core :refer [css]]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [garden.units :refer [px]]))

(defstyles options
  [:body
   {:color "WindowText"
    :font "caption"
    :display "flex"
    :margin 0
    :min-height "100vh"
    :justify-content "center"
    :align-items "center"
    :background-color "Background"
    :line-height 1.5}]
  [:.bracket {:font-size (px 24)}]
  [:h3 {:display "flex"
        :justify-content "space-between"
        :align-items "center"
        :flex "0 1 100%"}]
  [:.input {:width "100%"}]
  [:#options-form {:display "flex"
                   :width (px 180)
                   :flex-wrap "wrap"}]
  [:.block {:display "flex"
            :margin-bottom (px 10)
            :flex-wrap "wrap"
            :flex "0 1 100%"}]
  [:#buttons {:justify-content "space-between"}]

  [:.label {:flex "0 1 100%"}])


(defstyles browser-action
  [:body
   {:font "caption"
    :color "WindowText"
    :padding (px 10)
    :background-color "Background"}]
  [:h3 {:display "flex"
        :justify-content "space-between"
        :align-items "center"}]
  [:.bracket {:font-size (px 24)}])

(defn -main
  [& args]
  (let [filename (str "out/" (if (env :release) "release" "dev") "/css/options.css")]
    (io/make-parents filename)
    (spit filename (css {:pretty-print? (env :release)} options))
    (println (str "Wrote: " filename)))

  (let [filename (str "out/" (if (env :release) "release" "dev") "/css/browser-action.css")]
    (io/make-parents filename)
    (spit filename (css {:pretty-print? (env :release)} browser-action))
    (println (str "Wrote: " filename))))
