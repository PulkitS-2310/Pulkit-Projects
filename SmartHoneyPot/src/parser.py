import json
from pathlib import Path
from typing import Any


def parse_cowrie_log(file_path: str | Path) -> list[dict[str, Any]]:
    """Parse Cowrie JSON-lines logs into a list of dictionaries."""
    path = Path(file_path)
    events: list[dict[str, Any]] = []

    if not path.exists():
        raise FileNotFoundError(f"Log file not found: {path}")

    with path.open("r", encoding="utf-8") as file:
        for line_number, line in enumerate(file, start=1):
            line = line.strip()
            if not line:
                continue
            try:
                events.append(json.loads(line))
            except json.JSONDecodeError as error:
                print(f"Skipping invalid JSON on line {line_number}: {error}")

    return events


if __name__ == "__main__":
    logs = parse_cowrie_log("data/sample_cowrie.json")
    print(f"Parsed {len(logs)} events")
    for event in logs[:3]:
        print(event)
