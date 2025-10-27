-- Base table

-- :name create-todos-table
-- :command :execute
-- :result :raw
CREATE TABLE todos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL CHECK (length(trim(title)) > 0),
    completed BOOLEAN NOT NULL DEFAULT false,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 1
);
-- :name create-history-table
-- :command :execute
-- :result :raw
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

-- :name create-audit-table-callback
-- :command :execute
-- :result :raw
CREATE OR REPLACE FUNCTION todos_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO todos_history (id, title, completed, updated_at, version, txid)
    VALUES (OLD.id, OLD.title, OLD.completed, OLD.updated_at, OLD.version, txid_current());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- :name create-audit-table-trigger
-- :command :execute
-- :result :raw
CREATE TRIGGER todos_audit
AFTER UPDATE OR DELETE ON todos
FOR EACH ROW EXECUTE FUNCTION todos_audit_trigger();