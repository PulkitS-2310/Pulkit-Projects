# Smart Honeypot + AI Attacker Profiling

A defensive cybersecurity project that analyzes Cowrie-style honeypot logs, profiles attacker behavior, and displays results in a dashboard.

## Current Prototype

This first version uses local sample logs. It does not expose any service to the internet.

## Features

- Parse JSON-lines honeypot logs
- Count failed and successful login attempts
- Extract commands entered by attackers
- Classify attacker behavior
- Display results in a Streamlit dashboard

## Setup

```bash
python -m venv venv
source venv/bin/activate  # macOS/Linux
# OR
venv\Scripts\activate     # Windows

pip install -r requirements.txt
```

## Run Parser Test

```bash
python src/parser.py
```

## Run Dashboard

```bash
streamlit run dashboard/app.py
```

## Safety Note

This project is for defensive security education. Start locally before deploying any honeypot online.
