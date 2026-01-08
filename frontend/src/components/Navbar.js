import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout, isAuthenticated, hasRole } = useAuth();
    const navigate = useNavigate();

    const handleLogout = (e) => {
        e.preventDefault();
        logout();
        navigate('/login');
    };

    // Helper to check for admin role (handling both potential serialization formats)
    const isAdmin = () => hasRole('ADMIN') || hasRole('ROLE_ADMIN');

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
            <div className="container">
                <Link className="navbar-brand" to="/">Quiz App</Link>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav me-auto">
                        {isAuthenticated && (
                            <>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/quizzes">Browse Quizzes</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/quizzes/my">My Quizzes</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/my-rooms">My Rooms</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/results">My Results</Link>
                                </li>
                                {isAdmin() && (
                                    <>
                                        <li className="nav-item">
                                            <Link className="nav-link" to="/categories">Categories</Link>
                                        </li>
                                        <li className="nav-item">
                                            <Link className="nav-link" to="/users">Users</Link>
                                        </li>
                                    </>
                                )}
                            </>
                        )}
                    </ul>
                    <ul className="navbar-nav ms-auto">
                        {isAuthenticated ? (
                            <>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/profile">{user.name}</Link>
                                </li>
                                <li className="nav-item">
                                    <button type="button" className="btn btn-outline-light btn-sm ms-2" onClick={handleLogout}>Logout</button>
                                </li>
                            </>
                        ) : (
                            <>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/login">Login</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/register">Register</Link>
                                </li>
                            </>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;