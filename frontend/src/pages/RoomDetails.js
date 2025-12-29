import React, { useState, useEffect } from 'react';
import {useParams, useNavigate, Link} from 'react-router-dom';
import api from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';

const RoomDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    
    const [room, setRoom] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('info'); // info, participants, quizzes, results

    // Data for sub-features
    const [allUsers, setAllUsers] = useState([]);
    const [allQuizzes, setAllQuizzes] = useState([]);
    const [results, setResults] = useState([]);
    
    const [userSearch, setUserSearch] = useState('');
    const [quizSearch, setQuizSearch] = useState('');

    const fetchRoom = async () => {
        try {
            const response = await api.get(`/myrooms/${id}`);
            setRoom(response.data);
        } catch (err) {
            console.error('Failed to fetch room:', err);
            setError('Failed to load room details.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRoom();
    }, [id]);

    // Actions
    const fetchUsers = async () => {
        if (allUsers.length > 0) return;
        try {
            const res = await api.get('/users');
            setAllUsers(res.data);
        } catch(err) { console.error(err); }
    };

    const fetchQuizzes = async () => {
        if (allQuizzes.length > 0) return;
        try {
            // Fetch valid quizzes only? Or all? Usually we want valid ones for rooms.
            // Using /quizzes which returns valid ones by default for non-admins
            const res = await api.get('/quizzes'); 
            setAllQuizzes(res.data);
        } catch(err) { console.error(err); }
    };

    const fetchResults = async () => {
        try {
            const res = await api.get(`/myrooms/${id}/leaderboard`);
            setResults(res.data);
        } catch(err) { console.error(err); }
    };

    const handleTabChange = (tab) => {
        setActiveTab(tab);
        if (tab === 'participants') fetchUsers();
        if (tab === 'quizzes') fetchQuizzes();
        if (tab === 'results') fetchResults();
    };

    const addParticipant = async (userId) => {
        try {
            await api.patch(`/myrooms/${id}/users/${userId}`);
            fetchRoom(); // Refresh room data
        } catch (err) { alert('Failed to add user'); }
    };

    const removeParticipant = async (userId) => {
        try {
            await api.delete(`/myrooms/${id}/users/${userId}`);
            fetchRoom();
        } catch (err) { alert('Failed to remove user'); }
    };

    const addQuiz = async (quiz) => {
        // Check quiz status before making API call
        if (quiz.quizStatus !== 'VALID') {
            alert(`'${quiz.title}' cannot be added because it is not published. Please go to "My Quizzes" to publish it first.`);
            return;
        }
        try {
            await api.patch(`/myrooms/${id}/quizzes/${quiz.id}`);
            fetchRoom();
        } catch (err) {
            console.error('Failed to add quiz:', err);
            alert(`Failed to add quiz. Reason: ${err.response?.data?.message || 'Server error'}.`);
        }
    };

    const removeQuiz = async (quizId) => {
        try {
            await api.delete(`/myrooms/${id}/quizzes/${quizId}`);
            fetchRoom();
        } catch (err) { alert('Failed to remove quiz'); }
    };

    if (loading) return <div className="mt-5 text-center">Loading...</div>;
    if (error) return <div className="alert alert-danger mt-4">{error}</div>;
    if (!room) return <div className="alert alert-warning mt-4">Room not found</div>;

    const isOwner = room.owner?.id === user?.id; // Assuming user object has id
    const isAdmin = user?.roles?.includes('ADMIN') || user?.roles?.includes('ROLE_ADMIN');
    const canManage = isOwner || isAdmin;

    const now = new Date();
    const isActive = now >= new Date(room.startTime) && now <= new Date(room.endTime);

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>{room.roomName}</h2>
                <span className={`badge ${isActive ? 'bg-success' : 'bg-secondary'}`}>
                    {new Date(room.startTime).toLocaleString()} - {new Date(room.endTime).toLocaleString()}
                </span>
            </div>

            <ul className="nav nav-tabs mb-4">
                <li className="nav-item">
                    <button className={`nav-link ${activeTab === 'info' ? 'active' : ''}`} onClick={() => handleTabChange('info')}>Info</button>
                </li>
                <li className="nav-item">
                    <button className={`nav-link ${activeTab === 'participants' ? 'active' : ''}`} onClick={() => handleTabChange('participants')}>Participants</button>
                </li>
                <li className="nav-item">
                    <button className={`nav-link ${activeTab === 'quizzes' ? 'active' : ''}`} onClick={() => handleTabChange('quizzes')}>Quizzes</button>
                </li>
                <li className="nav-item">
                    <button className={`nav-link ${activeTab === 'results' ? 'active' : ''}`} onClick={() => handleTabChange('results')}>Results</button>
                </li>
            </ul>

            {/* TAB CONTENT */}
            
            {activeTab === 'info' && (
                <div className="card shadow-sm">
                    <div className="card-body">
                        <h5>Details</h5>
                        <p><strong>Owner:</strong> {room.owner?.name} ({room.owner?.email})</p>
                        <p><strong>Status:</strong> {new Date() < new Date(room.startTime) ? 'Not Started' : new Date() > new Date(room.endTime) ? 'Finished' : 'Active'}</p>
                        <hr />
                        <button className="btn btn-secondary" onClick={() => navigate('/my-rooms')}>Back to List</button>
                    </div>
                </div>
            )}

            {activeTab === 'participants' && (
                <div className="row">
                    <div className="col-md-6 mb-4">
                        <div className="card shadow-sm h-100">
                            <div className="card-header bg-light fw-bold">Current Participants ({room.participants?.length})</div>
                            <ul className="list-group list-group-flush">
                                {room.participants && room.participants.map(p => (
                                    <li key={p.id} className="list-group-item d-flex justify-content-between align-items-center">
                                        {p.name} <small className="text-muted">({p.email})</small>
                                        {canManage && (
                                            <button className="btn btn-sm btn-outline-danger" onClick={() => removeParticipant(p.id)}>Remove</button>
                                        )}
                                    </li>
                                ))}
                                {(!room.participants || room.participants.length === 0) && <li className="list-group-item text-muted">No participants yet.</li>}
                            </ul>
                        </div>
                    </div>
                    {canManage && (
                        <div className="col-md-6 mb-4">
                            <div className="card shadow-sm h-100">
                                <div className="card-header bg-light fw-bold">Add Participants</div>
                                <div className="p-2">
                                    <input 
                                        type="text" 
                                        className="form-control mb-2" 
                                        placeholder="Search users..." 
                                        value={userSearch}
                                        onChange={(e) => setUserSearch(e.target.value)}
                                    />
                                    <ul className="list-group" style={{maxHeight: '300px', overflowY: 'auto'}}>
                                        {allUsers.filter(u => u.name.toLowerCase().includes(userSearch.toLowerCase()) && !room.participants.some(rp => rp.id === u.id))
                                            .map(u => (
                                                <li key={u.id} className="list-group-item d-flex justify-content-between align-items-center">
                                                    {u.name}
                                                    <button className="btn btn-sm btn-success" onClick={() => addParticipant(u.id)}>Add</button>
                                                </li>
                                            ))
                                        }
                                    </ul>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            )}

            {activeTab === 'quizzes' && (
                <div className="row">
                    <div className="col-md-6 mb-4">
                        <div className="card shadow-sm h-100">
                            <div className="card-header bg-light fw-bold">Room Quizzes ({room.quizzes?.length})</div>
                            <ul className="list-group list-group-flush">
                                {room.quizzes && room.quizzes.map(q => (
                                    <li key={q.id} className="list-group-item d-flex justify-content-between align-items-center">
                                        <div>
                                            <strong>{q.title}</strong><br/>
                                            <small className="text-muted">{q.questions?.length || '?'} questions</small>
                                        </div>
                                        <div>
                                            {/* Link to play if active */}
                                            <button 
                                                className="btn btn-sm btn-primary"
                                                onClick={() => navigate(`/quizzes/${q.id}?roomId=${id}`)}
                                            >
                                                Play
                                            </button>
                                            {canManage && (
                                                <button className="btn btn-sm btn-outline-danger ms-2" onClick={() => removeQuiz(q.id)}>Remove</button>
                                            )}
                                        </div>
                                    </li>
                                ))}
                                {(!room.quizzes || room.quizzes.length === 0) && <li className="list-group-item text-muted">No quizzes added.</li>}
                            </ul>
                        </div>
                    </div>
                    {canManage && (
                        <div className="col-md-6 mb-4">
                            <div className="card shadow-sm h-100">
                                <div className="card-header bg-light fw-bold">Add Quizzes</div>
                                <div className="p-2">
                                    <input 
                                        type="text" 
                                        className="form-control mb-2" 
                                        placeholder="Search quizzes..." 
                                        value={quizSearch}
                                        onChange={(e) => setQuizSearch(e.target.value)}
                                    />
                                    <ul className="list-group" style={{maxHeight: '300px', overflowY: 'auto'}}>
                                        {allQuizzes.filter(q => q.title.toLowerCase().includes(quizSearch.toLowerCase()) && !room.quizzes.some(rq => rq.id === q.id))
                                            .map(q => (
                                                <li key={q.id} className="list-group-item d-flex justify-content-between align-items-center">
                                                    <div>
                                                        {q.title}
                                                        {q.quizStatus !== 'VALID' && <span className="badge bg-warning text-dark ms-2">Not Published</span>}
                                                    </div>
                                                    <button
                                                        className="btn btn-sm btn-success"
                                                        onClick={() => addQuiz(q)}
                                                    >
                                                        Add
                                                    </button>
                                                </li>
                                            ))
                                        }
                                    </ul>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            )}

            {activeTab === 'results' && (
                <div className="card shadow-sm">
                    <div className="card-header bg-light fw-bold">Results Board (Total Best Scores)</div>
                    <div className="table-responsive">
                        <table className="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th>User</th>
                                    <th>Total Score</th>
                                </tr>
                            </thead>
                            <tbody>
                                {results.length > 0 ? (
                                    results.map(r => (
                                        <tr key={r.userId}>
                                            <td>{r.userName}</td>
                                            <td>{r.totalScore}</td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr><td colSpan="2" className="text-center p-3 text-muted">No results yet.</td></tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
};

export default RoomDetails;
