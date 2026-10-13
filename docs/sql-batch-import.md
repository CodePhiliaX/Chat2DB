# Cancellable batch inserts and SQL script transactions

Import execution uses the existing task and SQL entrypoints. The JDBC insert path processes at most 500 statements per chunk, checks cancellation between chunks, and registers the active statement for cancellation. No new API, UI, account, storage provider or import manifest is required.

When a connection is in JDBC auto-commit mode and the script has not opened a transaction, each successful chunk is committed. A failed current chunk is rolled back; earlier committed chunks remain. The remaining chunks are not executed. If rollback or restoring the connection fails, the connection is discarded rather than returned in an uncertain state.

When the caller has disabled JDBC auto-commit, the caller retains its commit/rollback decision. An SQL script that executes `BEGIN` or `START TRANSACTION` also owns its transaction, even if the driver still reports JDBC auto-commit. Insert batches do not commit behind that script. `ROLLBACK TO [SAVEPOINT]` keeps the enclosing transaction open, and `COMMIT/ROLLBACK AND CHAIN` keeps the next transaction under script control. PostgreSQL `END` and `ABORT` aliases are covered by the same boundary. See the [PostgreSQL 16 transaction documentation](https://www.postgresql.org/docs/16/sql-rollback.html) and [savepoint rollback documentation](https://www.postgresql.org/docs/16/sql-rollback-to.html).

Ordinary CSV and Excel imports keep sequential execution so a row failure retains the already executed prefix. Their existing mode and format behavior is not changed into an atomic batch. Parallel insert paths continue to use the batch implementation.

## Verification

Use Java17 and the checked-in Maven reactor. The focused suite exercises actual JDBC calls, cancellation and transaction classification:

```powershell
mvn -B -f chat2db-community-server/pom.xml -pl ':chat2db-community-domain-core,:chat2db-community-spi' -am `
  '-Dmaven.test.skip=false' '-DskipTests=false' `
  '-Dtest=SqlTransactionScopeTest,ImportSqlExecutorScriptTransactionTest,DefaultSQLExecutorTaskCancellationTest' `
  '-Dsurefire.failIfNoSpecifiedTests=false' '-Dmaven.test.failure.ignore=false' clean test
```

The real PostgreSQL test drives UTF-8 SQL files through the production importer/parser into a newly created PostgreSQL16 fixture. It verifies commit, rollback, savepoints, chained transactions, an existing caller-owned transaction, aliases, and cancellation after a `pg_sleep` statement is confirmed running. The cancellation case verifies that no rows were committed.

```powershell
python -X utf8 script/test/test-sql-batch-postgres-runner.py
python -X utf8 script/test/run-sql-batch-postgres-it.py
```

The runner requires Docker, creates a named fixture on loopback, keeps its generated password in inherited process environments, verifies a fresh report with exactly eight executed tests and no failures/errors/skips, and removes only the container whose ownership label it verified. Missing/empty/failed/skipped reports fail the gate. Cleanup failures preserve a preceding Maven failure. It never reuses a user database.

Run the full owning-module gate to catch effects on ordinary imports and other callers:

```powershell
mvn -B -f chat2db-community-server/pom.xml -pl ':chat2db-community-domain-core,:chat2db-community-web' -am `
  '-Dmaven.test.skip=false' '-DskipTests=false' '-Dsurefire.includes=**/*Test.java' `
  '-Dmaven.test.failure.ignore=false' clean package
```

This feature's real-database evidence targets PostgreSQL16 and H2 JDBC contracts. Other engines, native installers and provider CI require their own execution evidence; they are not inferred from a successful package.
