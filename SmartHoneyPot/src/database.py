import sqlite3
from pathlib import Path
from typing import Any

DB_PATH = Path("honeypot.db")


def get_connection(db_path: str | Path = DB_PATH) -> sqlite3.Connection:
    return sqlite3.connect(db_path)


def initialize_database(db_path: str | Path = DB_PATH) -> None:
    with get_connection(db_path) as conn:
        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp TEXT,
                eventid TEXT,
                src_ip TEXT,
                username TEXT,
                password TEXT,
                session TEXT,
                command TEXT,
                duration REAL,
                raw_json TEXT
            )
            """
        )


def insert_events(events: list[dict[str, Any]], db_path: str | Path = DB_PATH) -> None:
    initialize_database(db_path)

    with get_connection(db_path) as conn:
        for event in events:
            conn.execute(
                """
                INSERT INTO events (
                    timestamp, eventid, src_ip, username, password,
                    session, command, duration, raw_json
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                (
                    event.get("timestamp"),
                    event.get("eventid"),
                    event.get("src_ip"),
                    event.get("username"),
                    event.get("password"),
                    event.get("session"),
                    event.get("input"),
                    event.get("duration"),
                    str(event),
                ),
            )
