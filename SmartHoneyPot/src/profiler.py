from collections import defaultdict
from typing import Any

SUSPICIOUS_DOWNLOAD_KEYWORDS = ["wget", "curl", "ftp", "tftp"]
RECON_KEYWORDS = ["uname", "whoami", "id", "ifconfig", "ip a", "cat /etc/passwd", "ls"]


def profile_attackers(events: list[dict[str, Any]]) -> list[dict[str, Any]]:
    attackers: dict[str, dict[str, Any]] = defaultdict(lambda: {
        "src_ip": "",
        "failed_logins": 0,
        "successful_logins": 0,
        "commands": [],
        "sessions": set(),
        "classification": "Unknown",
        "summary": "",
    })

    for event in events:
        ip = event.get("src_ip", "unknown")
        attacker = attackers[ip]
        attacker["src_ip"] = ip

        eventid = event.get("eventid", "")
        command = event.get("input", "")

        if eventid == "cowrie.login.failed":
            attacker["failed_logins"] += 1
        elif eventid == "cowrie.login.success":
            attacker["successful_logins"] += 1
        elif eventid == "cowrie.command.input" and command:
            attacker["commands"].append(command)

        if event.get("session"):
            attacker["sessions"].add(event["session"])

    results = []
    for attacker in attackers.values():
        commands = " ".join(attacker["commands"]).lower()
        failed = attacker["failed_logins"]
        success = attacker["successful_logins"]
        command_count = len(attacker["commands"])

        if any(keyword in commands for keyword in SUSPICIOUS_DOWNLOAD_KEYWORDS):
            classification = "Malware Downloader"
        elif command_count > 0 and any(keyword in commands for keyword in RECON_KEYWORDS):
            classification = "Reconnaissance Bot"
        elif failed >= 5 and success == 0:
            classification = "Brute-force Bot"
        elif success > 0:
            classification = "Interactive Intruder"
        else:
            classification = "Low Activity Scanner"

        attacker["classification"] = classification
        attacker["session_count"] = len(attacker["sessions"])
        attacker["sessions"] = list(attacker["sessions"])
        attacker["summary"] = build_summary(attacker)
        results.append(attacker)

    return results


def build_summary(attacker: dict[str, Any]) -> str:
    return (
        f"IP {attacker['src_ip']} had {attacker['failed_logins']} failed login attempts, "
        f"{attacker['successful_logins']} successful logins, and ran "
        f"{len(attacker['commands'])} commands. Classified as: {attacker['classification']}."
    )
