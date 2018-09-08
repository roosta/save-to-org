(ns yank.logging)

(defmacro log [& args]
  `(.log js/console ~@args))

(defmacro info [& args]
  `(.info js/console ~@args))

(defmacro warn [& args]
  `(.warn js/console ~@args))

(defmacro error [& args]
  `(.warn js/console ~@args))
