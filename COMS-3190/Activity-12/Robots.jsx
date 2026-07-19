import React, { useEffect, useState } from "react";
import { Card, Row, Col, Container } from "react-bootstrap";

const Robots = () => {
  const [robots, setRobots] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getRobots();
  }, []);

  const getRobots = async () => {
    try {
      const response = await fetch("http://127.0.0.1:8080/listRobots");

      if (!response.ok) {
        throw new Error("Failed to fetch robots.");
      }

      const data = await response.json();
      setRobots(data);
    } catch (err) {
      console.error("Error fetching robots:", err);
      setError(err.message);
    }
  };

  return (
    <Container>
      <h1 className="text-center mb-4">Robots</h1>

      {error && <p className="text-danger text-center">{error}</p>}

      <Row xs={1} md={2} lg={3} className="g-4">
        {robots.map((robot) => (
          <Col key={robot.id}>
            <Card className="h-100">
              <Card.Img
                variant="top"
                src={robot.imageUrl}
                alt={robot.name}
                style={{ height: "350px", objectFit: "contain", padding: "10px" }}
              />
              <Card.Body>
                <Card.Title>{robot.name}</Card.Title>
                <Card.Text>
                  <strong>Price:</strong> ${robot.price}
                </Card.Text>
                <Card.Text>
                  <strong>Description:</strong> {robot.description}
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
    </Container>
  );
};

export default Robots;