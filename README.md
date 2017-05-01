# alumbra.errors

This library provides formatting functions for [GraphQL][graphql] parser and
validation errors as described in [alumbra.spec][alumbra-spec].

[![Build Status](https://travis-ci.org/alumbra/alumbra.errors.svg?branch=master)](https://travis-ci.org/alumbra/alumbra.errors)
[![Clojars Project](https://img.shields.io/clojars/v/alumbra/errors.svg)](https://clojars.org/alumbra/errors)

[graphql]: http://graphql.org
[alumbra-spec]: https://github.com/alumbra/alumbra.spec

## Usage

```clojure
(require '[alumbra.errors :as errors])
```

An erroneous parser or validator result can be passed to `explain-data` to get
a unified result:

```clojure
(errors/explain-data
  (validator
    (parse/parse-document "{ me { unknownField } }")))
;; => [{:locations [{:row 0, :column 7, :index 7}]
;;      :context   "1|  { me { unknownField } }\n           ^"
;;      :message   "Syntax Error ..."
;;      :hint      "..."}]
```

The available keys are:

- `:locations`: a seq of error locations,
- `:context`: a formatted piece of the input query with an indicator as to where
  the error occured,
- `:message`: the parser/validation error message,
- `:hint`: if available, a small piece of text describing common causes and
  solutions for this class of error.

Additionally, there is `explain-str` and `explain`, formatting errors as a
string and printing it, respectively.

## License

```
MIT License

Copyright (c) 2017 Yannick Scherer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
