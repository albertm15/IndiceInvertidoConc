# Concurrent Inverted Index (Java)

## Description
This project is a Java implementation of a **Concurrent Inverted Index**. Its main goal is to process large collections of text files and index the words they contain along with their exact location (file and line number), enabling efficient search operations.

The project focuses on **concurrent programming** to improve indexing performance, using modern Java features such as **Virtual Threads** and concurrent data structures (`ConcurrentSkipListMap`).

## Features
- **Concurrent Indexing:** Uses multiple threads to process files in parallel.
- **Java Virtual Threads:** Modern implementation using `Thread.startVirtualThread` for highly scalable thread management.
- **Thread-Safe Data Structures:** Extensive use of `ConcurrentSkipListMap` and `ConcurrentSkipListSet` to avoid race conditions without complex explicit locking.
- **Persistence:** Ability to save the generated index to disk and load it later.
- **Search System (Query):** Allows querying a loaded index and computes matching percentages for the searched phrase.
- **Normalization:** Text is processed ignoring case, accents, and special characters.

## Requirements
- **Java JDK 21** or higher (required for native *Virtual Threads* support).
- **Apache Commons IO** (`commons-io`).
- **JUnit 5** (for assertions and internal tests).

## Project Structure

The source code is located in the `eps.scp` package:

- **`Indexing.java`**  
  Main class for index generation. Manages the complete workflow: argument parsing, timing, index construction, saving, and verification.

- **`InvertedIndex.java`**  
  Manager class that coordinates data structures and launches processing tasks. Contains the query logic.

- **`ProcesarDirectorio.java`**  
  A (`Runnable`) task that recursively traverses directories and launches a virtual thread for each text file found.

- **`ProcesarFichero.java`**  
  A (`Runnable`) task responsible for reading a file line by line, tokenizing words, and updating the shared concurrent index.

- **`Query.java`**  
  Independent main class used to perform searches on an already created index.

- **`Location.java`**  
  Helper class that stores the location of a word (file ID and line number).

## How to Run

### 1. Generate the Index (Indexing)

This tool traverses the input directory and generates the index files.

**Usage:**
```bash
java eps.scp.Indexing <Source_Directory> [<Index_Directory>]
```

- `<Source_Directory>`: Path to the folder containing `.txt` files to index.
- `<Index_Directory>`: (Optional) Path where the index will be stored.  
  Default: `./Index/`

**Example:**
```bash
java eps.scp.Indexing ./books ./my_index
```

### 2. Perform Queries (Query)

This tool loads an existing index and searches for a phrase.

**Usage:**
```bash
java eps.scp.Query "<Search phrase>" <Index_Directory>
```

**Example:**
```bash
java eps.scp.Query "The ingenious hidalgo" ./my_index
```

**Expected Output:**

The program will display the lines where the words appear and compute the match quality:

- **Full Matching:** 100% of the words found.
- **Matching:** More than 80% of the words found.
- **Weak Matching:** More than 60% of the words found.

## Concurrency Technical Details

The project implements a **data-parallelism model**:

- **Exploration:** `ProcesarDirectorio` explores the directory hierarchy.
- **Parallelization:** For each `.txt` file found, a **Virtual Thread** (`Thread.startVirtualThread`) is launched.  
  This allows handling thousands of files simultaneously with very low memory overhead.
- **Synchronization:**  
  No explicit `synchronized` blocks are used for word insertion. Instead, thread safety relies on `java.util.concurrent` collections:
  - The global map is a `ConcurrentSkipListMap`.
  - The map values are `ConcurrentSkipListSet`, which store ordered, duplicate-free locations.
