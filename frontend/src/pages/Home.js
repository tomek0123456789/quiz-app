import React from 'react';
import { useAuth } from '../context/AuthContext';

const Home = () => {
    const { user, logout } = useAuth();

    return (
        <div className="container mt-5">
            <h1>Welcome, {user.name}!</h1>
            <p>Your role: {user.roles.join(', ')}</p>
            <button className="btn btn-danger" onClick={logout}>Logout</button>
        </div>
    );
};

export default Home;
