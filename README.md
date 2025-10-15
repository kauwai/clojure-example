# Project Overview

This repository demonstrates a potential issue in how **clojure-lsp** resolves classpaths. Specifically, it appears that classpath entries may be resolved in an order different from what is expected.

## Description

When inspecting this project's classpath (via `clojure -Spath`), you will notice that both **leiningen** and **leiningen-core** appear in it.

Both of these libraries define the same namespace:  
`leiningen.core.project`

In classpath resolution, when two libraries provide the same namespace, the **first** one listed in the classpath should take precedence. Therefore, since `leiningen` appears before `leiningen-core`, any vars referenced from that namespace should resolve to the one provided by **leiningen**, not **leiningen-core**.

However, this project demonstrates that this expected behavior does not occur when using **clojure-lsp**.

Specifically, the var `read-raw`, defined in the namespace `leiningen.core.project`, is being resolved to the version from **leiningen-core**, even though **leiningen** appears first in the classpath.

## How to Reproduce

1. Check the classpath order:
   ```bash
   clojure -Spath

You should see that `leiningen` precedes `leiningen-core`.

2. Run the project:
   ```bash
   clojure -M -m main

The output will show the URL pointing to the JAR file where the var is being resolved from — in this case, the leiningen-core JAR.

