import React from 'react';
import Navbar from './Navbar';
import { Outlet } from 'react-router-dom';

const Layout = () => {
    return (
        <div className="d-flex flex-column min-vh-100">
            <Navbar />
            <main className="flex-grow-1">
                <div className="container">
                    <Outlet />
                </div>
            </main>
            <footer className="bg-light text-center py-3 mt-4">
                <div className="container">
                    <span className="text-muted">© 2025 Quiz App</span>
                </div>
            </footer>
        </div>
    );
};

export default Layout;
