import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "./PlannerPage.css";

const API_URL = "http://localhost:5000/api";

const MONTHS = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

const WEEKDAYS = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

function toKey(year, month, day) {
  return `${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(
    2,
    "0"
  )}`;
}

function getEmojiFromOutfit(outfit) {
  const firstItem = outfit?.items?.[0];

  if (!firstItem) return "👕";

  const category = firstItem.category || "";

  if (category === "Dresses") return "👗";
  if (category === "Shoes") return "👟";
  if (category === "Accessories") return "👜";
  if (category === "Outerwear") return "🧥";
  if (category === "Bottoms") return "👖";

  return "👕";
}

export default function PlannerPage() {
  const today = new Date();
  const todayKey = toKey(today.getFullYear(), today.getMonth(), today.getDate());

  const [year, setYear] = useState(today.getFullYear());
  const [month, setMonth] = useState(today.getMonth());

  const [outfits, setOutfits] = useState([]);
  const [entries, setEntries] = useState({});
  const [selected, setSelected] = useState(todayKey);

  const [showAdd, setShowAdd] = useState(false);
  const [addOutfitId, setAddOutfitId] = useState("");
  const [addNote, setAddNote] = useState("");

  const [editingEntryId, setEditingEntryId] = useState(null);
  const [editingNote, setEditingNote] = useState("");

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  async function loadPlannerData() {
    try {
      setLoading(true);
      setError("");

      const [outfitsRes, plannerRes] = await Promise.all([
        fetch(`${API_URL}/outfits?favorite=true`),
        fetch(`${API_URL}/planner`),
      ]);

      const outfitsData = await outfitsRes.json();
      const plannerData = await plannerRes.json();

      if (!outfitsRes.ok) {
        throw new Error(outfitsData.error || "Unable to load outfits.");
      }

      if (!plannerRes.ok) {
        throw new Error(plannerData.error || "Unable to load planner.");
      }

      setOutfits(outfitsData);

      const groupedEntries = {};

      plannerData.forEach((entry) => {
        if (!groupedEntries[entry.date]) {
          groupedEntries[entry.date] = [];
        }

        const matchingOutfit = outfitsData.find(
          (outfit) => outfit.id === entry.outfitId
        );

        groupedEntries[entry.date].push({
          id: entry.id,
          outfitId: entry.outfitId,
          outfitName: entry.outfitName,
          note: entry.notes || "",
          emoji: getEmojiFromOutfit(matchingOutfit),
        });
      });

      setEntries(groupedEntries);
    } catch (err) {
      setError(err.message || "Unable to load planner data.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPlannerData();
  }, []);

  const firstDay = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();

  const cells = Array.from({ length: firstDay + daysInMonth }, (_, index) =>
    index < firstDay ? null : index - firstDay + 1
  );

  while (cells.length % 7 !== 0) {
    cells.push(null);
  }

  function prevMonth() {
    if (month === 0) {
      setMonth(11);
      setYear((currentYear) => currentYear - 1);
    } else {
      setMonth((currentMonth) => currentMonth - 1);
    }
  }

  function nextMonth() {
    if (month === 11) {
      setMonth(0);
      setYear((currentYear) => currentYear + 1);
    } else {
      setMonth((currentMonth) => currentMonth + 1);
    }
  }

  function handleDayClick(day) {
    if (!day) return;

    setSelected(toKey(year, month, day));
    setShowAdd(false);
    setAddOutfitId("");
    setAddNote("");
    setEditingEntryId(null);
    setEditingNote("");
    setError("");
  }

  async function handleAddSave() {
    try {
      if (!addOutfitId || !selected) return;

      const outfit = outfits.find((item) => item.id === addOutfitId);

      if (!outfit) {
        setError("Selected outfit was not found.");
        return;
      }

      setSaving(true);
      setError("");

      const res = await fetch(`${API_URL}/planner`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          date: selected,
          outfitId: outfit.id,
          outfitName: outfit.name,
          notes: addNote,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to save planner entry.");
      }

      const newEntry = {
        id: data.id,
        outfitId: data.outfitId,
        outfitName: data.outfitName,
        note: data.notes || "",
        emoji: getEmojiFromOutfit(outfit),
      };

      setEntries((previousEntries) => ({
        ...previousEntries,
        [selected]: [...(previousEntries[selected] || []), newEntry],
      }));

      setAddOutfitId("");
      setAddNote("");
      setShowAdd(false);
    } catch (err) {
      setError(err.message || "Unable to save planner entry.");
    } finally {
      setSaving(false);
    }
  }

  function startEditEntry(entry) {
    setEditingEntryId(entry.id);
    setEditingNote(entry.note || "");
  }

  async function handleUpdateEntry(dateKey, entry) {
    try {
      setSaving(true);
      setError("");

      const res = await fetch(`${API_URL}/planner/${entry.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          notes: editingNote,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to update planner entry.");
      }

      setEntries((previousEntries) => ({
        ...previousEntries,
        [dateKey]: (previousEntries[dateKey] || []).map((currentEntry) =>
          currentEntry.id === entry.id
            ? { ...currentEntry, note: data.notes || "" }
            : currentEntry
        ),
      }));

      setEditingEntryId(null);
      setEditingNote("");
    } catch (err) {
      setError(err.message || "Unable to update planner entry.");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(dateKey, entryId) {
    try {
      setError("");

      const res = await fetch(`${API_URL}/planner/${entryId}`, {
        method: "DELETE",
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to delete planner entry.");
      }

      setEntries((previousEntries) => ({
        ...previousEntries,
        [dateKey]: (previousEntries[dateKey] || []).filter(
          (entry) => entry.id !== entryId
        ),
      }));
    } catch (err) {
      setError(err.message || "Unable to delete planner entry.");
    }
  }

  const selectedEntries = selected ? entries[selected] || [] : [];

  const monthKey = `${year}-${String(month + 1).padStart(2, "0")}`;
  const plannedDatesThisMonth = Object.keys(entries).filter((dateKey) =>
    dateKey.startsWith(monthKey)
  );

  return (
    <div className="page-layout">
      <Navbar />

      <main className="planner-page">
        <div className="page-container">
          <div className="planner__header">
            <div>
              <p className="section-eyebrow">Schedule</p>
              <h1 className="planner__title">Outfit Planner</h1>
              <p className="planner__sub">
                Assign outfits to specific dates for upcoming events.
              </p>
            </div>

            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: 8,
                fontSize: 12,
                color: "var(--ink-muted)",
              }}
            >
              <div
                style={{
                  width: 8,
                  height: 8,
                  borderRadius: "50%",
                  background: "var(--cta)",
                }}
              />
              Has outfit
            </div>
          </div>

          {error && (
            <div
              style={{
                marginBottom: 18,
                padding: 14,
                borderRadius: 12,
                border: "1px solid #efb3b3",
                background: "#fff0f0",
                color: "#8a1f1f",
                fontWeight: 700,
              }}
            >
              {error}
            </div>
          )}

          {loading ? (
            <div className="cal">
              <p>Loading planner...</p>
            </div>
          ) : (
            <>
              <div className="planner__body">
                <div className="cal">
                  <div className="cal__nav">
                    <button className="cal__nav-btn" onClick={prevMonth}>
                      ‹
                    </button>

                    <span className="cal__month-label">
                      {MONTHS[month]} <strong>{year}</strong>
                    </span>

                    <button className="cal__nav-btn" onClick={nextMonth}>
                      ›
                    </button>
                  </div>

                  <div className="cal__grid cal__grid--head">
                    {WEEKDAYS.map((weekday) => (
                      <div key={weekday} className="cal__weekday">
                        {weekday}
                      </div>
                    ))}
                  </div>

                  <div className="cal__grid">
                    {cells.map((day, index) => {
                      if (!day) {
                        return (
                          <div
                            key={`empty-${index}`}
                            className="cal__cell cal__cell--empty"
                          />
                        );
                      }

                      const dateKey = toKey(year, month, day);
                      const isToday = dateKey === todayKey;
                      const isSelected = dateKey === selected;
                      const hasPlan = Boolean(entries[dateKey]?.length);

                      return (
                        <button
                          key={dateKey}
                          className={`cal__cell ${
                            isToday ? "cal__cell--today" : ""
                          } ${isSelected ? "cal__cell--selected" : ""} ${
                            hasPlan ? "cal__cell--has" : ""
                          }`}
                          onClick={() => handleDayClick(day)}
                        >
                          <span className="cal__day-num">{day}</span>

                          {hasPlan && (
                            <div className="cal__dots">
                              <span className="cal__dot" />
                            </div>
                          )}
                        </button>
                      );
                    })}
                  </div>
                </div>

                <div className="planner__detail-col">
                  <div className="planner__day-panel">
                    <div className="planner__day-header">
                      <div>
                        <p className="section-eyebrow">
                          {new Date(selected + "T12:00:00").toLocaleDateString(
                            "en-US",
                            { weekday: "long" }
                          )}
                        </p>

                        <h2 className="planner__day-title">
                          {new Date(selected + "T12:00:00").toLocaleDateString(
                            "en-US",
                            {
                              month: "long",
                              day: "numeric",
                              year: "numeric",
                            }
                          )}
                        </h2>
                      </div>

                      <button
                        className="btn btn-primary btn-sm"
                        onClick={() => setShowAdd((current) => !current)}
                      >
                        + Add
                      </button>
                    </div>

                    {showAdd && (
                      <div className="planner__add-form">
                        <p
                          style={{
                            fontSize: 13,
                            fontWeight: 700,
                            marginBottom: 10,
                          }}
                        >
                          Schedule an outfit
                        </p>

                        <select
                          className="bform-input"
                          style={{ marginBottom: 8 }}
                          value={addOutfitId}
                          onChange={(event) => setAddOutfitId(event.target.value)}
                        >
                          <option value="">Choose an outfit…</option>

                          {outfits.map((outfit) => (
                            <option key={outfit.id} value={outfit.id}>
                              {getEmojiFromOutfit(outfit)} {outfit.name}
                            </option>
                          ))}
                        </select>

                        <input
                          type="text"
                          className="bform-input"
                          placeholder="Note (optional)"
                          value={addNote}
                          onChange={(event) => setAddNote(event.target.value)}
                          style={{ marginBottom: 10 }}
                        />

                        <div
                          style={{
                            display: "flex",
                            gap: 8,
                            justifyContent: "flex-end",
                          }}
                        >
                          <button
                            className="btn btn-secondary btn-sm"
                            onClick={() => setShowAdd(false)}
                          >
                            Cancel
                          </button>

                          <button
                            className="btn btn-primary btn-sm"
                            onClick={handleAddSave}
                            disabled={!addOutfitId || saving}
                          >
                            {saving ? "Saving…" : "Save"}
                          </button>
                        </div>
                      </div>
                    )}

                    {selectedEntries.length === 0 ? (
                      <div className="planner__empty-day">
                        No outfits planned for this day.
                      </div>
                    ) : (
                      <div className="planner__entries">
                        {selectedEntries.map((entry) => (
                          <div key={entry.id} className="planner__entry">
                            <div className="planner__entry-icon">
                              {entry.emoji}
                            </div>

                            <div className="planner__entry-info">
                              <p className="planner__entry-name">
                                {entry.outfitName}
                              </p>

                              {editingEntryId === entry.id ? (
                                <input
                                  className="bform-input"
                                  value={editingNote}
                                  onChange={(event) =>
                                    setEditingNote(event.target.value)
                                  }
                                  placeholder="Update note"
                                />
                              ) : (
                                entry.note && (
                                  <p className="planner__entry-note">
                                    {entry.note}
                                  </p>
                                )
                              )}
                            </div>

                            {editingEntryId === entry.id ? (
                              <button
                                className="planner__entry-del"
                                onClick={() => handleUpdateEntry(selected, entry)}
                              >
                                Save
                              </button>
                            ) : (
                              <button
                                className="planner__entry-del"
                                onClick={() => startEditEntry(entry)}
                              >
                                Edit
                              </button>
                            )}

                            <button
                              className="planner__entry-del"
                              onClick={() => handleDelete(selected, entry.id)}
                            >
                              ✕
                            </button>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              </div>

              <section className="planner__upcoming">
                <h2 className="planner__up-title">
                  Upcoming This Month
                  <span className="tag tag-blue" style={{ marginLeft: 12 }}>
                    {plannedDatesThisMonth.length} days planned
                  </span>
                </h2>

                <div className="planner__up-grid">
                  {Object.entries(entries)
                    .filter(([dateKey]) => dateKey.startsWith(monthKey))
                    .sort(([a], [b]) => a.localeCompare(b))
                    .map(([dateKey, dayEntries]) => (
                      <button
                        key={dateKey}
                        className={`planner__up-card ${
                          selected === dateKey ? "planner__up-card--active" : ""
                        }`}
                        onClick={() => setSelected(dateKey)}
                      >
                        <div className="planner__up-date">
                          <span className="planner__up-day">
                            {new Date(dateKey + "T12:00:00").getDate()}
                          </span>

                          <span className="planner__up-weekday">
                            {new Date(dateKey + "T12:00:00").toLocaleDateString(
                              "en-US",
                              { weekday: "short" }
                            )}
                          </span>
                        </div>

                        <div className="planner__up-outfits">
                          {dayEntries.map((entry) => (
                            <div key={entry.id} className="planner__up-outfit">
                              <span>{entry.emoji}</span>
                              <span>{entry.outfitName}</span>

                              {entry.note && (
                                <span className="planner__up-note">
                                  · {entry.note}
                                </span>
                              )}
                            </div>
                          ))}
                        </div>
                      </button>
                    ))}
                </div>
              </section>
            </>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}