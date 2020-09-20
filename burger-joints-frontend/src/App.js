import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

import VenueList from "./VenueList";
import Navbar from "react-bootstrap/Navbar";

function App() {
    return (
        <div className="App">
            <Navbar bg="dark" variant="dark" expand="lg" sticky="top">
                <Navbar.Brand href="#home"> <img
                    alt=""
                    src="burger-logo.svg"
                    width="30"
                    height="30"
                    className="d-inline-block align-top"
                />{' '}Burger Joints</Navbar.Brand>
            </Navbar>
            <VenueList/>
        </div>
    );
}

export default App;
