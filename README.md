# Re-Streamer

Clojure and ClojureScript Library for Reactive Programming

## Description

If you come from Java/JavaScript world, and you are RxJava/RxJS fan, this library will be your cup of tea.
However, if you like Clojure and reactive programming, Re-Streamer will make it much easier.

Major reactive entity in Re-Streamer library is a stream. There are two types of stream.
First, called `stream` is a base type. You can create it, emit values and subscribe in order to listen
to its state changes. Second is called `behavior-stream`. It has one difference to the base `stream`.
When `behavior-stream` is subscribed, its current state is emitted immediately to the subscriber function.

`behavior-stream` is used as a `store` in Re-Action framework. More details about Re-Action, you can find
[here](https://github.com/stanimirovic/re-action).

Re-Streamer provides following operators: `map`, `pluck`, `distinct`, `filter` and `skip`.
They are used for streams transformation and returns special type of stream called `subscriber`.
You can subscribe or get state from `subscriber`, but can't emit values to it directly. 
It listens to the parent (transformed) stream changes, and emits a value when new value is emitted to the parent stream.

## Usage

To use Re-Streamer in your Leiningen project, add following dependency in `project.clj`:

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.stanimirovic/re-streamer.svg)](https://clojars.org/org.clojars.stanimirovic/re-streamer)

## Examples

### Stream

### Behavior Stream

### Operators

#### Map

#### Pluck

#### Distinct

#### Filter

#### Skip

For more details, check [examples](https://github.com/stanimirovic/re-streamer/tree/master/examples) directory.

## License

Copyright © 2019 Marko Stanimirovic

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
