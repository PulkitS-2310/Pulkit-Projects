import sys
from pathlib import Path

import pandas as pd
import streamlit as st

PROJECT_ROOT = Path(__file__).resolve().parents[1]
sys.path.append(str(PROJECT_ROOT))

from src.parser import parse_cowrie_log
from src.profiler import profile_attackers

st.set_page_config(page_title="Smart Honeypot AI", layout="wide")

st.title("Smart Honeypot + AI Attacker Profiling")
st.write("Local prototype using sample Cowrie-style honeypot logs.")

log_path = PROJECT_ROOT / "data" / "sample_cowrie.json"
events = parse_cowrie_log(log_path)
profiles = profile_attackers(events)

events_df = pd.DataFrame(events)
profiles_df = pd.DataFrame(profiles)

col1, col2, col3 = st.columns(3)
col1.metric("Total Events", len(events_df))
col2.metric("Unique IPs", events_df["src_ip"].nunique())
col3.metric("Attacker Profiles", len(profiles_df))

st.subheader("Attacker Profiles")
st.dataframe(profiles_df[["src_ip", "failed_logins", "successful_logins", "classification", "summary"]])

st.subheader("Raw Honeypot Events")
st.dataframe(events_df)

st.subheader("Commands Observed")
command_events = events_df[events_df["eventid"] == "cowrie.command.input"]
if command_events.empty:
    st.info("No commands observed yet.")
else:
    st.dataframe(command_events[["timestamp", "src_ip", "session", "input"]])
