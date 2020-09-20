import React, {Component} from 'react';
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Card from "react-bootstrap/Card";
import Table from "react-bootstrap/Table";
import Alert from "react-bootstrap/Alert";

class VenueList extends Component {

    constructor(props) {
        super(props);

        this.state = {
            venues: [],
            isLoading: false,
            hasError: false,
            errorMessage: null
        }
    }

    async componentDidMount() {
        this.setState({isLoading: true});

        await fetch('http://localhost:8080/api/burgerjoints?place=Tartu')
            .then(response => {

                if (response.status === 429) {
                    throw new Error("Burger request quota exceeded");
                }

                return response.json();
            })
            .then(data => this.setState({venues: data, isLoading: false}))
            .catch(error => this.setState({hasError: true, errorMessage: error.message}));
    }

    render() {
        if (this.state.hasError) {
            return (
                <Container className='mt-5'>
                    <Alert variant="danger">
                        <Alert.Heading>Oh snap! You got an error!</Alert.Heading>
                        <p>
                            {this.state.errorMessage}
                        </p>
                    </Alert>
                </Container>
            );
        }

        const {venues, isLoading} = this.state;

        if (isLoading) {
            return <p>Loading...</p>
        }

        let items = [];

        for (let i = 0, j = venues.length; i < j; i += 3) {
            items.push(venues.slice(i, i + 3).map(venue =>
                <th key={venue.key}>
                    <Card bg="light">
                        {venue.burgerPhotoUrl ?
                            <Card.Img variant="top" src={venue.burgerPhotoUrl}/> :
                            <Card.Img variant="top" src="burger-not-found.png"/>
                        }
                        <Card.Body>
                            <Card.Title> {venue.name}</Card.Title>
                        </Card.Body>
                    </Card>
                </th>
            ));
        }

        return (
            <Container className='mt-5'>
                <Row>
                    <Col>
                        <Table borderless="true">
                            <tbody>
                            {items.map(item => <tr>{item}</tr>)}
                            </tbody>
                        </Table>
                    </Col>
                </Row>
            </Container>
        );
    }
}

export default VenueList;