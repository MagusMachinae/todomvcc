(ns todomvcc.shared.ui.view
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [todomvcc.shared.ui.events :as events]))

(defn nav-header []
  [:header
   {:class "header"}
   [:table
    {:class "width-auto"}
    [:tbody
     [:tr
      [:td
       {:class "width-auto"}
       [:h1 {:style {:text-transform "inherit !important"}} "TodoMVCC"]]
      [:td {:class "width-min"}
       [:a {:href "#interactive-demo"} "Demo"]
       [:a {:href "https://github.com/xtdb/todomvcc/blob/main/app-spec.md"}
        "Spec"]
       [:a {:href "https://github.com/xtdb/todomvcc/blob/main/postgres.md"}
        "PostgreSQL"]
       [:a {:href "https://xtdb.com"} "XTDB"]]]]]])

(defn input-pane [])

(defn result-container [result]
  [:p {:style {:overflow-wrap "break-word"}} 
   (str ">>> " result)])

(defn results-pane []
  (let [history (rf/subscribe [::events/query-results])]
    [:div {:style {:overflow "scroll"}}
     [:ul 
      (for [result  history]
        
        [result-container result])]]))

(defn interactive-demo []
  [:div [:h2 "Interactive Demo" {:id "interactive-demo"}]
   [:grid]
   
   [:p "The demo will showcase side-by-side comparisons demonstrating:"]
   [:ol
    [:li "Creating todos with different effective dates"]
    [:li "Concurrent updates to the same todo item"]
    [:li "Historical queries to see past states"]
    [:li "Transaction isolation behavior under load"]
    [:li "Manual versioning vs automatic bitemporal tracking"]]
   [:p [:em "🚧 Interactive demo coming soon..."]]])

(defn body []
  [:div 
   [:p [:strong "TodoMVCC"] "= like TodoMVC but for databases (not UIs!)"]
   [:p
    "Developers building data-intensive applications often struggle with versioning, concurrency control, and maintaining consistency over time. Manual versioning schemes, transaction anomalies, and locking contention plague traditional approaches."]
   [:p
    "This project demonstrates these challenges using everyone&#39;s favorite example: a"
    [:strong "todo list"]
    "."]
   [:hr]
   [:h2 "The Problem"]
   [:p "Building reliable applications often means wrestling with:"]
   [:ul
    [:li
     [:strong "Manual versioning schemes"]
     "- Complex application logic"]
    [:li
     [:strong "Non-serializable transaction anomalies"]
     "- Lost updates, phantom reads"]
    [:li
     [:strong "Complex locking strategies"]
     "- Deadlocks and contention"]
    [:li
     [:strong "Custom audit trail implementation"]
     "- Triggers and history tables"]]
   [:p "What if there was a better way?"]
   [:hr]
   [:h2 "Why SERIALIZABLE Matters"]
   [:p
    "Most applications run on"
    [:strong "READ COMMITTED"]
    "isolation by default, which allows various consistency anomalies. Let&#39;s see how these manifest in a simple todo app:"]
   [:details
    [:summary "🐛 Lost Update Example"]
    [:p "Two users editing the same todo simultaneously:"]
    [:pre
     [:code
      "-- User A starts editing todo #123\n
BEGIN;\n
SELECT title FROM todos WHERE id = 123;  -- &quot;Buy groceries&quot;\n
\n
-- User B also starts editing the same todo\n
BEGIN;\n
SELECT title FROM todos WHERE id = 123;  -- &quot;Buy groceries&quot;\n
\n
-- User A updates the title\n
UPDATE todos SET title = &#39;Buy groceries and milk&#39; WHERE id = 123;\n
COMMIT;\n
\n
-- User B updates based on stale data\n
UPDATE todos SET title = &#39;Buy groceries and bread&#39; WHERE id = 123;\n
COMMIT;\n
\n
-- Result: User A&#39;s changes are lost!"]]
    [:p
     [:strong "In READ COMMITTED:"]
     "User B&#39;s update overwrites User A&#39;s changes."]
    [:p
     [:strong "In SERIALIZABLE:"]
     "One transaction would be aborted and retry with fresh data."]]
   [:details
    [:summary "📊 Write Skew Example"]
    [:p "Maintaining a business constraint across multiple todos:"]
    [:pre
     [:code
      "-- Rule: At most 3 todos can be marked &quot;urgent&quot; priority\n
-- Currently 2 urgent todos exist\n
\n
-- Transaction A: Mark todo #456 as urgent\n
BEGIN;\n
SELECT COUNT(*) FROM todos WHERE priority = &#39;urgent&#39;;  -- Returns 2\n
-- User thinks: &quot;OK, I can add one more&quot;\n
UPDATE todos SET priority = &#39;urgent&#39; WHERE id = 456;\n
\n
-- Transaction B: Mark todo #789 as urgent\n
BEGIN;\n
SELECT COUNT(*) FROM todos WHERE priority = &#39;urgent&#39;;  -- Returns 2\n
-- User thinks: &quot;OK, I can add one more&quot;\n
UPDATE todos SET priority = &#39;urgent&#39; WHERE id = 789;\n
\n
-- Both transactions commit successfully\n
-- Result: 4 urgent todos! Business rule violated."]]
    [:p
     [:strong "In READ COMMITTED:"]
     "Both transactions see consistent snapshots but create inconsistent final state."]
    [:p
     [:strong "In SERIALIZABLE:"]
     "One transaction would be aborted to maintain the constraint."]]
   [:p
    [:strong "SERIALIZABLE"]
    "isolation eliminates these anomalies by ensuring transactions appear to execute one at a time, even when running concurrently. For todo apps, this means:"]
   [:ul
    [:li
     [:strong "No lost updates"]
     "when multiple users edit the same todo"]
    [:li [:strong "Consistent business rules"] "across multiple todos"]
    [:li
     [:strong "Reliable audit trails"]
     "without manual version management"]
    [:li [:strong "Predictable behavior"] "under concurrent load"]]
   [:details
    [:summary "📚 Learn More About Consistency"]
    [:p
     "For a deep dive into consistency models and isolation levels, check out"
     [:a
      {:href "https://jepsen.io/consistency"}
      [:strong "Jepsen&#39;s Consistency Models"]]
     "- an excellent interactive guide to understanding how different databases handle consistency."]
    [:p "Key resources:"]
    [:ul
     [:li
      [:a
       {:href "https://jepsen.io/consistency/models/serializable"}
       "Jepsen on Serializability"]]
     [:li
      [:a
       {:href "https://jepsen.io/consistency/models/snapshot-isolation"}
       "Snapshot Isolation vs Serializability"]]
     [:li
      [:a
       {:href "https://jepsen.io/consistency"}
       "Interactive Consistency Explorer"]]]]
   [:hr]
   [:h2 "Two Approaches"]
   [:details
    [:summary "🐘 Traditional SQL (PostgreSQL)"]
    [:p "Manual versioning with locking-based concurrency control."]
    [:h3 "Characteristics:"]
    [:ul
     [:li "Manual version columns and triggers"]
     [:li "Read committed isolation issues"]
     [:li "Lock contention under load"]
     [:li "Complex audit trail implementation"]
     [:li "Lost update and phantom read anomalies"]]
    [:h3 "Example Schema:"]
    [:pre
     [:code
      "CREATE TABLE todos (\n
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),\n
    title TEXT NOT NULL,\n
    completed BOOLEAN NOT NULL DEFAULT false,\n
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),\n
    version BIGINT NOT NULL DEFAULT 1\n
);\n
\n
CREATE TABLE todos_history (\n
    id UUID NOT NULL,\n
    title TEXT NOT NULL,\n
    completed BOOLEAN NOT NULL,\n
    updated_at TIMESTAMPTZ NOT NULL,\n
    version BIGINT NOT NULL,\n
    txid BIGINT NOT NULL DEFAULT txid_current(),\n
    PRIMARY KEY (id, version)\n
);"]]
    [:p
     [:a {:href "postgres.md"} "📖 View Complete PostgreSQL Example →"]]]
   [:details
    [:summary "🔥 XTDB Bitemporal"]
    [:p "Built-in time dimensions with optimistic concurrency control."]
    [:h3 "Characteristics:"]
    [:ul
     [:li "Automatic bitemporal versioning"]
     [:li "Serializable transactions by default"]
     [:li "Lock-free optimistic concurrency"]
     [:li "Complete audit trail built-in"]
     [:li "No anomalies, perfect consistency"]]
    [:h3 "Example Query:"]
    [:pre
     [:code
      "-- Time travel query (built-in)\n
SELECT * FROM todos\n
FOR SYSTEM_TIME AS OF &#39;2024-01-01T12:00:00Z&#39;\n
WHERE completed = false;\n
\n
-- No manual versioning needed!\n
INSERT INTO todos (id, title, completed)\n
VALUES (1, &#39;Learn XTDB&#39;, false);"]]
    [:p [:em "XTDB implementation coming soon..."]]]
   [:hr]
   [interactive-demo]
   [:hr]
   [:h2 "Real-World Impact"]
   [:table
    [:thead
     [:tr
      [:th "Challenge"]
      [:th "Traditional SQL"]
      [:th "XTDB Bitemporal"]]]
    [:tbody
     [:tr
      [:td [:strong "Version Tracking"]]
      [:td "Complex application logic"]
      [:td "Zero application code"]]
     [:tr
      [:td [:strong "Concurrency"]]
      [:td "Deadlocks and timeouts"]
      [:td "No locks, no contention"]]
     [:tr
      [:td [:strong "Consistency"]]
      [:td "Race conditions possible"]
      [:td "Perfect isolation"]]
     [:tr
      [:td [:strong "Audit Trail"]]
      [:td "Manual triggers/tables"]
      [:td "Built-in auditability"]]
     [:tr
      [:td [:strong "Performance"]]
      [:td "Degradation with locking"]
      [:td "Predictable characteristics"]]]]
   [:hr]
   [:h2 "Implementation Guide"]
   [:p
    "Want to contribute an implementation? Check out the"
    [:a
     {:href "https://github.com/xtdb/todomvcc/blob/main/app-spec.md"}
     [:strong "TodoMVCC Specification"]]
    "for guidelines on:"]
   [:ul
    [:li "Core data model requirements"]
    [:li "Required CRUD operations"]
    [:li "Concurrent scenarios to demonstrate"]
    [:li "Directory structure and documentation"]]
   [:hr]
   [:h2 "Educational Use"]
   [:p "TodoMVCC is perfect for:"]
   [:ul
    [:li
     [:strong "University courses"]
     "on database systems and concurrency"]
    [:li
     [:strong "Technical talks"]
     "about MVCC and bitemporal databases"]
    [:li
     [:strong "Team training"]
     "on data consistency and versioning patterns"]
    [:li [:strong "Architecture discussions"] "about database selection"]]
   [:hr]
   [:p
    "Built by"
    [:a {:href "https://xtdb.com"} "XTDB"]
    "|"
    [:a {:href "https://github.com/xtdb/todomvcc"} "View on GitHub"]]
   [:p
    [:em
     "TodoMVCC combines the familiar TodoMVC concept with Multi-Version Concurrency Control - because sometimes the best way to understand complex database concepts is through a simple, relatable example that every developer knows: a todo list."]]])
