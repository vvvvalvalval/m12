A project experimenting with 12-digits notation for music.

Set up with the [devcards](https://github.com/bhauman/devcards) leiningen template.
Adapted to use the Figwheel REPL in combination with the Cursive IDE.

Currently uses [AudioSynth](https://github.com/keithwhor/audiosynth) for playing music in the browser.

## Dev workflow

### Setup

You need Leiningen, Npm and Gulp installed.

Installing dependencies:

`$ lein deps && npm install`

Making files executable

`$ chmod +x *.sh`

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

### Deploying to production

```
$ ./deploy.sh
```

## TODO

* welcome page - explain experiment
* abstract out game logic - general component for that. Register exercise.
* build: cleaning for prod interferes with dev - maybe separate folders.