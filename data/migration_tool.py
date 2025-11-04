import argparse
import os
import sqlite3
import sys
from typing import List, Tuple, Optional

from dotenv import load_dotenv


load_dotenv()
SQL_MIGRATION_DIR = os.getenv("DB_MIGRATION_PATH")
DB_URL = os.getenv("DB_HOST_PATH")

class MigrationTool:
    """Tool to manage database migrations (up/down) for an SQLite database"""

    # List of tuples where each entry is (migration_file, rollback_file)
    MIGRATION_FILES: List[Tuple[str, str]] = [
        ("v1_create_transactions_table.sql", "u1_create_transactions_table.sql")
    ]

    _project_root: str = ""
    _db_path: str = ""

    def __init__(self, root_dir: str):
        self._project_root = root_dir
        self._db_path = os.path.join(self._project_root, DB_URL)

    def _connect(self) -> sqlite3.Connection:
        """Establishes and returns a connection to the SQLite database"""
        return sqlite3.connect(self._db_path)

    def _create_schema_version_table(self, connection: sqlite3.Connection):
        """Creates the SchemaVersion table if it doesn't exist"""
        sql = """
            CREATE TABLE IF NOT EXISTS SchemaVersion
            (
                version TEXT PRIMARY KEY,
                created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """

        connection.execute(sql)
        connection.commit()

    def _record_applied_migration(self, connection: sqlite3.Connection, version: str):
        """Inserts a record into the SchemaVersion table"""
        sql = "INSERT INTO SchemaVersion (version) VALUES (?)"

        connection.execute(sql, (version,))
        connection.commit()

    def _get_last_applied_migration(self, connection: sqlite3.Connection) -> Optional[str]:
        """Retrieves the version of the last applied migration"""
        sql = "SELECT version FROM SchemaVersion ORDER BY created DESC LIMIT 1"

        cursor = connection.execute(sql)
        row = cursor.fetchone()
        return row[0] if row else None

    def _delete_applied_migration_record(self, connection: sqlite3.Connection, version: str):
        """Deletes a record from the SchemaVersion table (used for rollback)"""
        sql = "DELETE FROM SchemaVersion WHERE version = ?"
        connection.execute(sql, (version,))
        connection.commit()

    def _read_sql_script(self, file_name: str) -> str:
        """Reads the content of an SQL script file"""
        file_dir = os.path.join(self._project_root, SQL_MIGRATION_DIR)
        file_path = os.path.join(file_dir, file_name)

        try:
            with open(file_path, 'r') as f:
                return f.read()
        except FileNotFoundError as e:
            raise RuntimeError(f"Failed to read SQL migration script: {file_path}") from e
        except Exception as e:
            raise RuntimeError(f"An error occurred while reading script {file_name}: {e}") from e

    def _find_index_of_key(self, key: Optional[str]) -> int:
        """Finds the index of a migration file's name (key) in MIGRATION_FILES"""
        if key is None:
            return -1

        for i, (migration_file, _) in enumerate(self.MIGRATION_FILES):
            if migration_file == key:
                return i
        return -1

    def up(self):
        """Applies pending migrations"""
        try:
            with self._connect() as connection:
                print(f"DB connected at {self._db_path}")

                self._create_schema_version_table(connection)

                last_migration = self._get_last_applied_migration(connection)
                last_migration_index = self._find_index_of_key(last_migration)

                for i in range(last_migration_index + 1, len(self.MIGRATION_FILES)):
                    migration, _ = self.MIGRATION_FILES[i]

                    print(f"Applying migration: {migration}")
                    sql = self._read_sql_script(migration)

                    connection.executescript(sql)
                    self._record_applied_migration(connection, migration)
                    connection.commit()

                print("Migrations applied successfully")

        except sqlite3.Error as e:
            print(f"Database error during 'up' command: {e}", file=sys.stderr)
        except Exception as e:
            print(f"Unexpected error occurred during 'up' command: {e}", file=sys.stderr)

    def down(self):
        """Reverts the last applied migrations"""
        try:
            with self._connect() as connection:
                print(f"DB connected at {self._db_path}")

                self._create_schema_version_table(connection)

                last_migration = self._get_last_applied_migration(connection)
                last_migration_index = self._find_index_of_key(last_migration)

                if last_migration_index == -1:
                    print("No migrations to rollback")
                    return

                # Rollback the last applied one
                migration, rollback = self.MIGRATION_FILES[last_migration_index]

                print(f"Reverting migration: {migration} (using {rollback})")
                sql = self._read_sql_script(rollback)

                connection.executescript(sql)
                self._delete_applied_migration_record(connection, migration)
                connection.commit()

                print("Migrations reverted successfully")

        except sqlite3.Error as e:
            print(f"Database error during 'down' command: {e}", file=sys.stderr)
        except Exception as e:
            print(f"Unexpected error occurred during 'down' command: {e}", file=sys.stderr)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(allow_abbrev=False, add_help=False)
    parser.add_argument(
        "--root-dir",
        type=str,
        required=True,
        help="The absolute path to the project root directory"
    )
    parser.add_argument(
        "command",
        type=str,
        choices=["up", "down"],
        help="Migration command"
    )

    args = parser.parse_args()
    project_root = args.root_dir.strip('"')
    command = args.command

    tool = MigrationTool(project_root)
    if command == "up":
        tool.up()
    elif command == "down":
        tool.down()
