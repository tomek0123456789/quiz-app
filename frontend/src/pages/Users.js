import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

const Users = () => {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const fetchUsers = async () => {
        try {
            const response = await api.get('/users');
            setUsers(response.data);
        } catch (err) {
            setError('Failed to fetch users.');
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleDeactivate = async (id) => {
        if (!window.confirm('Are you sure you want to deactivate this user?')) return;

        try {
            await api.delete(`/users/${id}`);
            // Optimistically update the user status
            setUsers(users.map(u => u.id === id ? { ...u, status: 'DEACTIVATED' } : u));
            setSuccess('User deactivated.');
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError('Failed to deactivate user.');
            setTimeout(() => setError(''), 3000);
        }
    };

    return (
        <div className="container mt-4">
            <h2>User Management</h2>
            {error && <div className="alert alert-danger">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            <div className="card shadow-sm">
                <table className="table table-hover mb-0">
                    <thead className="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Roles</th>
                            <th>Status</th>
                            <th className="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map(user => (
                            <tr key={user.id}>
                                <td className="align-middle">{user.id}</td>
                                <td className="align-middle">{user.name}</td>
                                <td className="align-middle">{user.email}</td>
                                <td className="align-middle">{user.roles ? user.roles.join(', ') : ''}</td>
                                <td className="align-middle">
                                    <span className={`badge ${user.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}`}>
                                        {user.status}
                                    </span>
                                </td>
                                <td className="text-end">
                                    {user.status === 'ACTIVE' && (
                                        <button 
                                            className="btn btn-sm btn-outline-danger"
                                            onClick={() => handleDeactivate(user.id)}
                                        >
                                            Deactivate
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                        {users.length === 0 && (
                            <tr>
                                <td colSpan="6" className="text-center text-muted">No users found.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Users;
