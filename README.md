# ![TodoMVCC](https://via.placeholder.com/200x50/667eea/ffffff?text=TodoMVCC)

> Helping you understand Multi-Version Concurrency Control

### [Live Demo](https://xtdb.github.io/todomvcc)&nbsp;&nbsp;&nbsp;&nbsp;[App Spec](app-spec.md)&nbsp;&nbsp;&nbsp;&nbsp;[PostgreSQL](postgres.md)&nbsp;&nbsp;&nbsp;&nbsp;[XTDB](https://xtdb.com)&nbsp;&nbsp;&nbsp;&nbsp;[Blog](https://xtdb.com/blog)

[![GitHub Pages](https://img.shields.io/badge/GitHub-Pages-green.svg?style=flat-square)](https://xtdb.github.io/todomvcc)
[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)](LICENSE)

Developers building data-intensive applications often struggle with versioning, concurrency control, and maintaining consistency over time. Manual versioning schemes, transaction anomalies, and locking contention plague traditional approaches.

PostgreSQL with manual versioning, XTDB with bitemporal MVCC... the approaches to handling data over time vary dramatically, but how do you understand the real-world implications of these architectural choices?

To help demonstrate these differences, we created TodoMVCC - a project which shows the same Todo application data model implemented using different concurrency control approaches, highlighting the practical implications of each.

## What You'll Learn

TodoMVCC demonstrates the fundamental differences between:

- **Manual Versioning** vs **Automatic Bitemporal Tracking**
- **Locking-based Concurrency** vs **Optimistic Concurrency Control**
- **Transaction Anomalies** vs **Serializable Consistency**
- **Complex Audit Trails** vs **Built-in Time Travel**

## Implementations

### Traditional SQL Approach ([PostgreSQL](postgres.md))
- Manual version columns and application logic
- Read Committed isolation with potential anomalies
- Lock-based concurrency control
- Custom audit trail implementation
- Complex handling of concurrent updates

### Bitemporal Approach (XTDB)
- Automatic transaction-time and valid-time tracking
- Serializable isolation by default
- Optimistic concurrency control (lock-free)
- Built-in audit trail and time travel queries
- Simple, consistent handling of all temporal scenarios

## Live Examples

Each implementation demonstrates:

- ✅ **Creating todos** with different effective dates
- ⚡ **Concurrent updates** to the same todo item
- 🕰️ **Historical queries** to see past states
- 🔄 **Transaction isolation** behavior under load
- 📊 **Performance characteristics** with multiple users

## Getting Started

```bash
# Clone the repository
git clone https://github.com/xtdb/todomvcc.git
cd todomvcc

# View the live demo
open https://xtdb.github.io/todomvcc

# Or serve locally
python -m http.server 8000
open http://localhost:8000
```

## Project Structure

```
todomvcc/
├── index.html          # Landing page and concept explanation
├── app-spec.md         # TodoMVCC specification for implementers
├── postgres.md         # PostgreSQL implementation with manual versioning
├── postgres/          # PostgreSQL schema and query files (coming soon)
├── xtdb/             # XTDB bitemporal implementation (coming soon)
├── shared/           # Common UI components and utilities (coming soon)
└── docs/            # Additional documentation (coming soon)
```

## The TodoMVCC Pun

TodoMVCC combines the familiar TodoMVC concept with Multi-Version Concurrency Control (MVCC) - because sometimes the best way to understand complex database concepts is through a simple, relatable example that every developer knows: a todo list.

## Team

TodoMVCC is brought to you by the team at [XTDB](https://xtdb.com), builders of the bitemporal SQL database.

#### [XTDB Team](https://github.com/xtdb) - Project Maintainers

XTDB is dedicated to making bitemporal data simple and accessible. We believe that all applications can benefit from built-in versioning, audit trails, and time travel capabilities without the complexity traditionally associated with these features.

## Disclaimer

TodoMVCC has been designed to illustrate the fundamental differences in concurrency control approaches using a familiar application model. While the Todo application offers a simplified view of database capabilities, it effectively demonstrates the core challenges developers face with versioning and concurrency.

This project is meant to be used as a gateway to understanding how different approaches to MVCC can dramatically impact application complexity, performance, and reliability. We recommend exploring both implementations and considering how these patterns apply to your specific use cases.

Please keep in mind that TodoMVCC focuses on conceptual clarity rather than production optimization. The examples are designed to highlight architectural differences rather than serve as performance benchmarks.

## Getting Involved

We're interested in contributions that help illustrate the differences between concurrency control approaches. Whether you're a database expert, application developer, or just curious about MVCC, there are ways to contribute:

- 🐛 **Report issues** with the examples or explanations
- 💡 **Suggest improvements** to the demonstrations
- 📖 **Contribute documentation** explaining additional concepts
- 🔧 **Add implementations** showing other MVCC approaches

Check out our [TodoMVCC specification](app-spec.md) for implementation guidelines and [contribution guidelines](CONTRIBUTING.md) for more info.

## Educational Use

TodoMVCC is perfect for:

- **University courses** on database systems and concurrency
- **Technical talks** about MVCC and bitemporal databases
- **Team training** on data consistency and versioning patterns
- **Architecture discussions** about database selection

## License

Everything in this repo is MIT License unless otherwise specified.

[MIT](LICENSE) © 2025 XTDB.
