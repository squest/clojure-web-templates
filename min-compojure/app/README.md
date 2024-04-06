# app

Minimal compojure setup, ini untuk up and running easily

Main libraries:
1. Compojure
2. Ring-defaults to provide basic middleware
3. Immutant web server
4. some other ring utilities to send JSON or HTML (Using selmer)

Supporting libraries => mostly to serve app.utils (sets of utility functions)
1. Cheshire to read-write json-edn
2. Selmer for html templating
3. clj-uuid for generating uuid
4. java-time for timing

## Usage

Lein repl => (start)
Available routes can be checked in app.handler

## License

Copyright © 2024 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
