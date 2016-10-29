A project experimenting with 12-digits notation for music.

Set up with the [devcards](https://github.com/bhauman/devcards) leiningen template.
Adapted to use the Figwheel REPL in combination with the Cursive IDE.

Currently uses [AudioSynth](https://github.com/keithwhor/audiosynth) for playing music in the browser.

## Dev workflow

You need Leiningen, Npm and Gulp installed.

Installing dependencies:

`$ lein deps && npm install`

### Running the dev environment

Figwheel REPL: if you're using Cursive, see [here](https://github.com/bhauman/lein-figwheel/wiki/Running-figwheel-in-a-Cursive-Clojure-REPL).
 Otherwise, in the command line: 
 
```
$ lein repl
# [wait for it ...]
user => (load-file "./scripts/cursive/figwheel-repl.clj")
```

Everything else:
 
```
$ gulp dev
```

### Building for production

```
$ lein cljsbuild once prod
$ gulp less-dev
```

## TODO

* Netlify deploy
* experiment with IFn to make partial fns with = semantics
* welcome page - explain experiment
* in production: minify CSS
