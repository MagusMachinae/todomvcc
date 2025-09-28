# PostgreSQL Example (TodoMVCC)

This implementation demonstrates traditional SQL approaches to versioning and concurrency control using PostgreSQL's MVCC capabilities with manual versioning.

## Database Setup

### Prerequisites
- PostgreSQL 12+ (for `gen_random_uuid()`)
- `psql` command-line client

### Installation
```bash
createdb todomvcc
psql todomvcc -f schema.sql
```

## Schema (`schema.sql`)

```sql
-- Base table
CREATE TABLE todos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL CHECK (length(trim(title)) > 0),
    completed BOOLEAN NOT NULL DEFAULT false,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 1
);

-- History table (append-only)
CREATE TABLE todos_history (
    id UUID NOT NULL,
    title TEXT NOT NULL,
    completed BOOLEAN NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL,
    txid BIGINT NOT NULL DEFAULT txid_current(),
    PRIMARY KEY (id, version)
);

-- Trigger: copy rows into history on every update/delete
CREATE OR REPLACE FUNCTION todos_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO todos_history (id, title, completed, updated_at, version, txid)
    VALUES (OLD.id, OLD.title, OLD.completed, OLD.updated_at, OLD.version, txid_current());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER todos_audit
AFTER UPDATE OR DELETE ON todos
FOR EACH ROW EXECUTE FUNCTION todos_audit_trigger();
```

## Core Operations (`queries.sql`)

### Create
```sql
INSERT INTO todos (title) VALUES ('Write spec')
RETURNING *;
```

### Read
```sql
-- All todos
SELECT * FROM todos ORDER BY updated_at DESC;

-- Only active
SELECT * FROM todos WHERE completed = false ORDER BY updated_at DESC;

-- Only completed
SELECT * FROM todos WHERE completed = true ORDER BY updated_at DESC;
```

### Update
```sql
-- Toggle complete + increment version
UPDATE todos
SET completed = NOT completed,
    updated_at = now(),
    version = version + 1
WHERE id = '12345678-1234-1234-1234-123456789abc'
RETURNING *;

-- Edit title
UPDATE todos
SET title = 'Updated title',
    updated_at = now(),
    version = version + 1
WHERE id = '12345678-1234-1234-1234-123456789abc'
RETURNING *;
```

### Delete
```sql
DELETE FROM todos WHERE id = '12345678-1234-1234-1234-123456789abc';
```

## Historical Queries (`historical.sql`)

### View History of a Todo
```sql
-- Complete version history
SELECT * FROM todos_history
WHERE id = '12345678-1234-1234-1234-123456789abc'
ORDER BY version;

-- Include current state
SELECT id, title, completed, updated_at, version, txid, 'current' as state
FROM todos WHERE id = '12345678-1234-1234-1234-123456789abc'
UNION ALL
SELECT id, title, completed, updated_at, version, txid, 'history' as state
FROM todos_history WHERE id = '12345678-1234-1234-1234-123456789abc'
ORDER BY version;
```

### State as of a Transaction ID
```sql
-- Todo state as of specific transaction
SELECT * FROM todos_history
WHERE id = '12345678-1234-1234-1234-123456789abc'
  AND txid <= 12345
ORDER BY version DESC
LIMIT 1;

-- All todos as of specific transaction
WITH historical_state AS (
    SELECT DISTINCT ON (id) *
    FROM todos_history
    WHERE txid <= 12345
    ORDER BY id, version DESC
)
SELECT * FROM historical_state
ORDER BY updated_at DESC;
```

## Concurrency Scenarios (`concurrency.sql`)

### Example: Two Clients Racing to Update the Same Row

**Session A:**
```sql
BEGIN;
SELECT * FROM todos WHERE id = '12345678-1234-1234-1234-123456789abc' FOR UPDATE;
-- (holds lock, simulating user editing)
```

**Session B:**
```sql
BEGIN;
UPDATE todos
SET title = 'New title from Session B',
    updated_at = now(),
    version = version + 1
WHERE id = '12345678-1234-1234-1234-123456789abc';
-- This blocks until Session A commits/rollbacks
```

**Back in Session A:**
```sql
UPDATE todos
SET title = 'New title from Session A',
    updated_at = now(),
    version = version + 1
WHERE id = '12345678-1234-1234-1234-123456789abc';
COMMIT;
```

**Result:**
- Session B's update applies after Session A commits
- Version increments show the order of updates
- History table preserves both versions with transaction IDs

### Optimistic Concurrency Control (Alternative)
```sql
-- Application-level optimistic locking
UPDATE todos
SET title = 'Updated title',
    updated_at = now(),
    version = version + 1
WHERE id = '12345678-1234-1234-1234-123456789abc'
  AND version = 5  -- Expected current version
RETURNING *;

-- If no rows affected, version conflict occurred
```

## MVCC Behavior and Limitations

### PostgreSQL's MVCC Strengths
- **Readers never block writers** (and vice versa)
- **Snapshot isolation** provides consistent reads
- **Row-level locking** for fine-grained control

### Manual Versioning Challenges
- **Application complexity**: Must manage version increments
- **Trigger maintenance**: History tracking requires database triggers
- **Concurrency handling**: Need explicit locking strategies
- **Storage overhead**: Duplicate data in history tables
- **Query complexity**: Joining current and historical data

### Common Issues
1. **Lost updates** without proper locking or optimistic concurrency
2. **Version conflicts** require application-level handling
3. **Trigger failures** can break history tracking
4. **Transaction ID wraparound** affects long-term historical queries

## Comparison with XTDB

| Aspect | PostgreSQL + Manual Versioning | XTDB Bitemporal |
|--------|--------------------------------|-----------------|
| **Version Tracking** | Manual `version` column + triggers | Automatic transaction-time |
| **History Storage** | Separate `_history` table | Built-in temporal storage |
| **Concurrency** | Locking or optimistic checks | Lock-free optimistic |
| **Time Travel** | Custom queries with transaction IDs | Native temporal queries |
| **Setup Complexity** | High (triggers, history tables) | Low (works out of the box) |
| **Query Complexity** | Complex JOINs for temporal data | Simple `AS OF` queries |

## Running the Examples

1. **Setup database:**
   ```bash
   createdb todomvcc
   psql todomvcc -f schema.sql
   ```

2. **Insert test data:**
   ```sql
   INSERT INTO todos (title) VALUES ('Learn PostgreSQL MVCC');
   INSERT INTO todos (title) VALUES ('Compare with XTDB');
   INSERT INTO todos (title) VALUES ('Build TodoMVCC demo');
   ```

3. **Test concurrency** (open two `psql` sessions and run the concurrency examples)

4. **Explore history:**
   ```sql
   UPDATE todos SET completed = true WHERE title = 'Learn PostgreSQL MVCC';
   SELECT * FROM todos_history;
   ```

---

*This example demonstrates the complexity of implementing versioning and audit trails manually in traditional SQL databases, highlighting why purpose-built temporal databases like XTDB can simplify application development.*