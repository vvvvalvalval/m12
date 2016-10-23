;; see https://github.com/bhauman/lein-figwheel/wiki/Running-figwheel-in-a-Cursive-Clojure-REPL
(use 'figwheel-sidecar.repl-api)
(start-figwheel! #_{:all-builds (figwheel-sidecar.repl/get-project-cljs-builds)
                  :figwheel-options {:css-dirs ["resources/public/css"]}})
(cljs-repl)
