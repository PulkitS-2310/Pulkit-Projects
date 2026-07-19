import 'bootstrap/dist/css/bootstrap.css';
import { Container, Card } from 'react-bootstrap';

export function UserCard(props) {
  return (
    <Container className="p-4 d-flex justify-content-center">
      <Card style={{ width: "25rem" }}>
        <Card.Img variant="top" src={props.picture} />

        <Card.Body>
          <Card.Title>{props.name}</Card.Title>

          <Card.Text>
            Salary: $ {props.amount}
          </Card.Text>

          <Card.Text>
            Married: {props.married ? "Yes" : "No"}
          </Card.Text>

          <Card.Text>
            Address: {props.address.street}, {props.address.city}, {props.address.state}
          </Card.Text>

        </Card.Body>
      </Card>
    </Container>
  );
}