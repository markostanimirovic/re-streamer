# Re-Streamer

Clojure and ClojureScript Library for Reactive Programming

## Description

If you come from Java/JavaScript world, and you are RxJava/RxJS fan, this library will be your cup of tea.
However, if you like Clojure and reactive programming, Re-Streamer will make it much easier.

Re-Streamer can be used in Clojure as well as in ClojureScript. Also, Re-Streamer has integration with
[Reagent](https://github.com/reagent-project/reagent) and when it is used in ClojureScript,
the state of the stream is stored in the Reagent atom.

Major reactive entity in Re-Streamer library is a stream. There are two types of streams.
Firstly, we have `stream` which is a base type. You can create it, emit values and subscribe in order to listen
to its state changes. Second is called `behavior-stream`. It has one difference to the base `stream`.
When `behavior-stream` is subscribed, its current state is emitted immediately to the new subscription.

`behavior-stream` is used as a `store` in Re-Action framework. You can find more details about Re-Action
[here](https://github.com/stanimirovic/re-action).

Re-Streamer provides following operators: `map`, `pluck`, `distinct`, `filter` and `skip`.
They are used for stream transformation and they return a special kind of stream called `subscriber`.
You can subscribe or get state from `subscriber`, but you can't emit values to it directly. 
It listens to the parent (transformed) stream changes, and emits a value when new value is emitted to the parent stream.

## Usage

To use Re-Streamer in your Leiningen project, add following dependency in `project.clj`:

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.stanimirovic/re-streamer.svg)](https://clojars.org/org.clojars.stanimirovic/re-streamer)

## Examples

Core Re-Streamer functionalities will be explained through the examples below.
For more details, check [examples](https://github.com/stanimirovic/re-streamer/tree/master/examples) directory.

### Stream

`stream` is a major part of this library. Let's dive deep into it through the example.
First, we need to create the fruits stream.

```clojure
(ns example.stream
  (:require [re-streamer.core :as re-streamer :refer [subscribe unsubscribe destroy emit flush]])
  (:refer-clojure :rename {flush c-flush}))

(def fruits (re-streamer/stream))  
```

However, we can pass the initial state's value to the stream. If we don't pass it, initial state will be `nil`.
Now, let's subscribe to our fruits stream in order to listen to the state changes.

```clojure
(def fruits-sub (subscribe fruits #(println (str "Fruits: " %))))
```

`subscribe` accepts the stream as a first argument, and a function that will be executed with stream's new emitted
value. Next step is to emit a value to the stream.

```clojure
(emit fruits "Apple")
```

We emit the string `Apple`, and do you see something in the console?
Printed value in the console will be:

```
Fruits: Apple
```

because our subscription prints that value. Also, we can add multiple subscriptions to our stream.
Let's add one more and emit new fruit.

```clojure
(def fruits2-sub (subscribe fruits #(println (str "Fruits 2: " %))))
(emit fruits "Orange")
```

Now, both subscriptions will be executed and in the console will be printed:

```
Fruits: Orange
Fruits 2: Orange
```

At every moment, we can get the current state of our stream.

```clojure
(println @(:state fruits))
```

Of course, `Orange` will be printed.

In case we no longer need first `fruits2-sub`, we can call `unsubscribe` function to remove it.

```clojure
(unsubscribe fruits fruits2-sub)
```

Let's emit a new value again.

```clojure
(emit fruits "Banana")
```

In the console only `Fruits: Banana` will be printed, because as stated before, second subscription is
removed from `fruits` subscriptions list.

Lastly, it is necessary to mention `flush` and `destroy` functions.
We can use `flush` function if we want to remove all subscriptions.

```clojure
(flush fruits)
```

When `flush` is called, all subscriptions are removed, but the stream is still alive.
In case we want to destroy the stream, `destroy` function is the right choice.

```clojure
(destroy fruits)
```

### Behavior Stream

As already stated, `behavior-stream` is very similar to `stream`.
When `stream` is subscribed, new subscription will be executed when the new state is emitted to that stream.
On the other hand, when `behavior-stream` is subscribed, new subscription will be executed immediately with
the current state. Further behavior of `stream` and `behavior-stream` will be the same.
When `behavior-stream` is subscribed, its current state is emitted immediately to the new subscription.

Let's now walk through the example.

```clojure
(ns example.behavior-stream
  (:require [re-streamer.core :as re-streamer :refer [subscribe emit]]))

(def number (re-streamer/behavior-stream 10))
```

First we need to create new `behavior-stream` called `number` with initial value `10`.
As in the case with `stream`, if we don't pass the initial value, it will be `nil`.
Let's now subscribe to it.

```clojure
(subscribe number #(println (str "Incremented number: " (inc %))))
```

In the console, immediately will be printed:

```
Incremented number: 11
```

Next, we will emit a new number.

```clojure
(emit number 100)
```

As you can guess, printed value in the console will be:

```
Incremented number: 101
```

Of course, you can use `unsubscribe`, `flush` and `destroy` functions and they will have the same behavior
as when used with `stream`.

### Operators

Re-Streamer provides `map`, `pluck`, `distinct`, `filter` and `skip` operators.
Every operator accepts the stream as a first argument. Second argument is the stream transformer.
Transformer is usually a function, but it can also be data depending on the operator.

#### Map

![Map Operator](https://github.com/stanimirovic/re-streamer/blob/master/resources/img/map-operator.png)

#### Pluck

![Pluck Operator](https://github.com/stanimirovic/re-streamer/blob/master/resources/img/pluck-operator.png)

#### Distinct

![Distinct Operator](https://github.com/stanimirovic/re-streamer/blob/master/resources/img/distinct-operator.png)

#### Filter

![Filter Operator](https://github.com/stanimirovic/re-streamer/blob/master/resources/img/filter-operator.png)

#### Skip

![Skip Operator](https://github.com/stanimirovic/re-streamer/blob/master/resources/img/skip-operator.png)

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
