import React, { useState } from "react";

const DeleteRobot = () => {
  const [robotId, setRobotId] = useState("");

  const handleChange = (event) => {
    setRobotId(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const url = `http://127.0.0.1:8080/deleteRobot/${robotId}`;

    try {
      const response = await fetch(url, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Failed to delete robot.");
      }

      alert("Robot deleted successfully!");
      setRobotId("");
    } catch (error) {
      console.error("Error:", error);
      alert(`Error deleting robot: ${error.message}`);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={robotId}
        onChange={handleChange}
        placeholder="Enter Robot ID"
        required
      />
      <br />
      <br />
      <button type="submit">Delete Robot</button>
    </form>
  );
};

export default DeleteRobot;