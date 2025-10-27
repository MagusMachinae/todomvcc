/*Create*/
INSERT INTO todos (title) VALUES (:title)
RETURNING *;

/*Read*/

-- :name list-todos
SELECT * FROM todos ORDER BY updated_at DESC;

-- :name list-active-todos
-- Only active
SELECT * FROM todos WHERE completed = false ORDER BY updated_at DESC;

-- :name list-completed-todos
-- Only completed
SELECT * FROM todos WHERE completed = true ORDER BY updated_at DESC;

/*Update*/

-- :name toggle-completion
-- :doc Toggle complete + increment version
UPDATE todos
SET completed = NOT completed,
    updated_at = now(),
    version = version + 1
WHERE id = :id
RETURNING *;

-- :name edit-title
UPDATE todos
SET title = 'Updated title',
    updated_at = now(),
    version = version + 1
WHERE id = :id
RETURNING *;

/*Delete*/
-- :name delete-todo
DELETE FROM todos WHERE id = :id;


-- Complete version history
SELECT * FROM todos_history
WHERE id = :id
ORDER BY version;

-- Include current state
SELECT id, title, completed, updated_at, version, txid, 'current' as state
FROM todos WHERE id = :id
UNION ALL
SELECT id, title, completed, updated_at, version, txid, 'history' as state
FROM todos_history WHERE id = :id
ORDER BY version;


-- Todo state as of specific transaction
SELECT * FROM todos_history
WHERE id = :id
  AND txid <= :tx-id
ORDER BY version DESC
LIMIT 1;

-- All todos as of specific transaction
WITH historical_state AS (
    SELECT DISTINCT ON (id) *
    FROM todos_history
    WHERE txid <= :tx-id
    ORDER BY id, version DESC
)
SELECT * FROM historical_state
ORDER BY updated_at DESC;
