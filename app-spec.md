# TodoMVCC Specification (v1.0)

We've created this spec to guide database implementers in building consistent, comparable examples of a todo app. The goal: demonstrate isolation, concurrency, versioning, and historical state across different databases.

## Core Data Model

Every database example must implement the following logical schema:

| Column | Type | Notes |
|--------|------|-------|
| `id` | Primary Key | Unique identifier per todo (UUID, SERIAL, or database-native equivalent) |
| `title` | String | Non-empty, trimmed |
| `completed` | Boolean | Defaults to false |
| `updated_at` | Timestamp | Last modification time |

### Optional Extension Fields

For showcasing database-specific features:

- **`version`** (Postgres/SQL): Incremental version number
- **`valid_from` / `valid_to`** (SQLite): Manual bitemporal tracking
- **XTDB-style system fields**: transaction-time, valid-time

Implementers are encouraged to highlight novel concurrency/versioning features of their database, but the core schema must be present.

## Required Operations

All implementations must support:

### Create Todo
Insert a new todo with a non-empty title. `completed` defaults to false.

### Read Todos
- Fetch all todos
- Filter by `completed = true/false`

### Update Todo
- Toggle `completed`
- Edit `title`
- Update `updated_at`
- Increment `version` or record transaction-time if database supports it

### Delete Todo
Remove a todo by `id`.

### Historical Queries (where supported)
Query the state of a todo or the list as of a past timestamp or transaction.
- Postgres/SQLite can emulate this via versioning or valid-time columns
- XTDB and bitemporal DBs do this natively

## Concurrent Scenarios

Each example must demonstrate:

1. **Two clients attempting overlapping updates**
2. **Isolation behavior** (e.g., blocking, serialization errors, last-write-wins)
3. **Optional**: multi-step transactions with rollback

## Directory Structure

Recommended file layout per database:

```
/<database-name>/
  schema.sql          -- DDL or equivalent
  queries.sql         -- CRUD + concurrency examples
  historical.sql      -- Past-state queries (optional if supported)
  README.md           -- Setup, behavior notes, MVCC quirks
```

## README Requirements

Each database example must document:

- Database type and version
- Concurrency and isolation model
- Transaction semantics and MVCC behavior
- How historical queries work (if applicable)
- Setup instructions (docker-compose, CLI, or REPL)
- Any "bonus features" (e.g., automatic bitemporal queries, append-only logs)

## Optional / Showcase Features

To highlight your database's uniqueness:

- **Postgres**: SERIALIZABLE isolation, xmin, row versioning
- **SQLite**: WAL mode, serialized writes
- **XTDB**: Bitemporal queries, immutable history
- **CouchDB / NoSQL**: Multi-version documents, conflict resolution

These extensions should not break comparability — core CRUD and filtering must remain consistent.

## Guiding Principles

### Uniformity first, novelty second
All examples must be comparable on the core CRUD, filtering, and basic concurrency behavior.

### Clarity and reproducibility
Examples must be runnable locally, with minimal setup.

### Illustrative over exhaustive
The goal is to show behavior, not build a production-ready todo app.

### Accessibility
Include comments or notes explaining MVCC, isolation, and historical queries for readers unfamiliar with these concepts.

## ✅ Summary

- **Core schema** ensures comparability
- **CRUD + historical queries + concurrent edits** demonstrate isolation/versioning
- **Optional fields** allow databases to "show off" their features
- **Directory + README requirements** make contributions uniform and easy to digest

---

*This specification is designed to create educational, comparable examples that highlight the practical differences in how databases handle concurrency, versioning, and time.*