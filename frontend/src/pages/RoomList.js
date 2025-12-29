import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';

const RoomList = () => {
    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { user } = useAuth(); // Get the current user

    const fetchRooms = async () => {
        setLoading(true);
        try {
            const response = await api.get('/myrooms');
            setRooms(response.data.sort((a, b) => a.id - b.id)); // Sort for consistent order
            setError('');
        } catch (err) {
            console.error('Error fetching rooms:', err);
            setError('Failed to load rooms.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRooms();
    }, []);

    const handleDelete = async (roomId) => {
        if (!window.confirm('Are you sure you want to delete this room?')) return;
        try {
            await api.delete(`/myrooms/${roomId}`);
            setRooms(rooms.filter(room => room.id !== roomId));
        } catch (err) {
            console.error('Error deleting room:', err);
            alert('Failed to delete room.');
        }
    };

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>My Rooms</h2>
                <Link to="/rooms/create" className="btn btn-success">
                    + Create New Room
                </Link>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {loading ? (
                <div className="text-center mt-5">Loading...</div>
            ) : (
                <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    {rooms.length > 0 ? (
                        rooms.map(room => {
                            const isOwner = user && user.id === room.owner?.id;
                            const isAdmin = user?.roles?.includes('ADMIN') || user?.roles?.includes('ROLE_ADMIN');
                            const canManage = isOwner || isAdmin;
                            
                            const now = new Date();
                            const isActive = now >= new Date(room.startTime) && now <= new Date(room.endTime);
                            
                            return (
                                <div className="col" key={room.id}>
                                    <div className={`card h-100 shadow-sm ${isActive ? 'border-success' : ''}`}>
                                        <div className="card-body">
                                            <h5 className="card-title">{room.roomName}</h5>
                                            <p className={`card-text ${isActive ? 'text-success fw-bold' : ''}`}>
                                                <strong>Owner:</strong> {room.owner?.name}<br />
                                                <strong>Start:</strong> {new Date(room.startTime).toLocaleString()}<br />
                                                <strong>End:</strong> {new Date(room.endTime).toLocaleString()}
                                            </p>
                                            <p className="card-text">
                                                <small className="text-muted">
                                                    Participants: {room.participants?.length || 0} | Quizzes: {room.quizzes?.length || 0}
                                                </small>
                                            </p>
                                        </div>
                                        <div className="card-footer bg-transparent border-top-0 d-flex justify-content-between">
                                            <Link to={`/rooms/${room.id}`} className="btn btn-primary btn-sm">
                                                Enter Room
                                            </Link>
                                            {canManage && (
                                                <div>
                                                    <Link to={`/rooms/edit/${room.id}`} className="btn btn-outline-secondary btn-sm me-2">
                                                        Edit
                                                    </Link>
                                                    <button 
                                                        className="btn btn-outline-danger btn-sm"
                                                        onClick={() => handleDelete(room.id)}
                                                    >
                                                        Delete
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            );
                        })
                    ) : (
                        <div className="col-12 text-center text-muted mt-5">
                            <p>You are not in any rooms. Create one or ask to be added!</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default RoomList;