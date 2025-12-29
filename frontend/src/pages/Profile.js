import React, { useState } from 'react';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Profile = () => {
    const { user, login, logout } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: user.email,
        password: ''
    });
    const [message, setMessage] = useState({ text: '', type: '' });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            const payload = { userId: user.id };
            if (formData.email !== user.email) payload.email = formData.email;
            if (formData.password) payload.password = formData.password;

            await api.put('/users', payload);
            
            // Refresh local user data
            // Since backend doesn't return the updated user on PUT /users, we assume success or fetch /me
            // But if email changes, token claims might be invalid if they rely on it?
            // Token uses email as subject. If email changes, token IS invalid.
            if (formData.email !== user.email) {
                alert('Email updated. Please login again.');
                logout();
                navigate('/login');
                return;
            }

            setMessage({ text: 'Profile updated successfully.', type: 'success' });
            
            // Optimistic update of local user context if simple fields
            login({ ...user, email: formData.email }, localStorage.getItem('token'));
            setFormData({ ...formData, password: '' });

        } catch (err) {
            setMessage({ text: 'Failed to update profile.', type: 'danger' });
        }
    };

    const handleDeactivate = async () => {
        if (!window.confirm('Are you sure you want to deactivate your account? You will not be able to login.')) return;
        
        try {
            await api.delete(`/users/${user.id}`);
            alert('Account deactivated.');
            logout();
            navigate('/login');
        } catch (err) {
            alert('Failed to deactivate account.');
        }
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <div className="card shadow-sm">
                        <div className="card-header bg-primary text-white">
                            <h4 className="mb-0">My Profile</h4>
                        </div>
                        <div className="card-body">
                            {message.text && <div className={`alert alert-${message.type}`}>{message.text}</div>}
                            
                            <div className="mb-4">
                                <p><strong>Name:</strong> {user.name}</p>
                                <p><strong>Role:</strong> {user.roles.join(', ')}</p>
                                <p><strong>Status:</strong> {user.status}</p>
                            </div>

                            <hr />
                            <h5>Update Information</h5>
                            <form onSubmit={handleUpdate}>
                                <div className="mb-3">
                                    <label className="form-label">Email Address</label>
                                    <input 
                                        type="email" 
                                        className="form-control"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="mb-3">
                                    <label className="form-label">New Password (leave blank to keep current)</label>
                                    <input 
                                        type="password" 
                                        className="form-control"
                                        name="password"
                                        value={formData.password}
                                        onChange={handleChange}
                                    />
                                </div>
                                <button type="submit" className="btn btn-primary">Update Profile</button>
                            </form>

                            <hr className="mt-5" />
                            <div className="d-flex justify-content-between align-items-center">
                                <span className="text-muted">Danger Zone</span>
                                <button className="btn btn-outline-danger" onClick={handleDeactivate}>
                                    Deactivate Account
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Profile;
