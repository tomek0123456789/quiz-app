import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axiosConfig';

// Helper to format date for datetime-local input
const toDateTimeLocal = (isoString) => {
    if (!isoString) return '';
    const date = new Date(isoString);
    // Adjust for timezone offset
    const timezoneOffset = date.getTimezoneOffset() * 60000;
    const localDate = new Date(date.getTime() - timezoneOffset);
    return localDate.toISOString().slice(0, 16);
};

const RoomEditor = () => {
    const { id } = useParams(); // Get room ID for editing
    const navigate = useNavigate();
    const isEditing = Boolean(id);

    const [roomData, setRoomData] = useState({
        name: '',
        startTime: '',
        endTime: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (isEditing) {
            setLoading(true);
            const fetchRoom = async () => {
                try {
                    const response = await api.get(`/myrooms/${id}`);
                    const { roomName, startTime, endTime } = response.data;
                    setRoomData({
                        name: roomName,
                        startTime: toDateTimeLocal(startTime),
                        endTime: toDateTimeLocal(endTime)
                    });
                } catch (err) {
                    setError('Failed to load room data.');
                } finally {
                    setLoading(false);
                }
            };
            fetchRoom();
        }
    }, [id, isEditing]);

    const handleChange = (e) => {
        setRoomData({ ...roomData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        const start = new Date(roomData.startTime);
        const end = new Date(roomData.endTime);
        
        if (start >= end) {
            setError('End time must be after start time.');
            return;
        }

        const payload = {
            name: roomData.name,
            startTime: start.toISOString(),
            endTime: end.toISOString()
        };

        try {
            if (isEditing) {
                // Backend PATCH endpoint uses RoomPatchDto which includes the id
                await api.patch('/myrooms', { id: parseInt(id), ...payload });
            } else {
                await api.post('/myrooms', payload);
            }
            navigate('/my-rooms');
        } catch (err) {
            console.error('Failed to save room:', err);
            setError(`Failed to ${isEditing ? 'update' : 'create'} room.`);
        }
    };

    if (loading) return <div className="text-center mt-5">Loading room...</div>;

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8 col-lg-6">
                    <div className="card shadow-sm">
                        <div className="card-body">
                            <h3 className="mb-4">{isEditing ? 'Edit Room' : 'Create New Room'}</h3>
                            {error && <div className="alert alert-danger">{error}</div>}
                            <form onSubmit={handleSubmit}>
                                <div className="mb-3">
                                    <label className="form-label">Room Name</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        name="name"
                                        value={roomData.name}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="mb-3">
                                    <label className="form-label">Start Time</label>
                                    <input
                                        type="datetime-local"
                                        className="form-control"
                                        name="startTime"
                                        value={roomData.startTime}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="mb-3">
                                    <label className="form-label">End Time</label>
                                    <input
                                        type="datetime-local"
                                        className="form-control"
                                        name="endTime"
                                        value={roomData.endTime}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                                <div className="d-flex justify-content-end gap-2">
                                    <button 
                                        type="button" 
                                        className="btn btn-secondary" 
                                        onClick={() => navigate('/my-rooms')}
                                    >
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        {isEditing ? 'Update Room' : 'Create Room'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RoomEditor;
