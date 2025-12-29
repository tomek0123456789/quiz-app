import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { Link } from 'react-router-dom';

const MyQuizzes = () => {
    const [quizzes, setQuizzes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchMyQuizzes = async () => {
        setLoading(true);
        try {
            const response = await api.get('/quizzes/my');
            // Sort by ID to ensure a stable order
            const sortedQuizzes = response.data.sort((a, b) => a.id - b.id);
            setQuizzes(sortedQuizzes);
            setError('');
        } catch (err) {
            setError('Failed to fetch your quizzes');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMyQuizzes();
    }, []);

    const handleDelete = async (quizId) => {
        if (window.confirm('Are you sure you want to delete this quiz?')) {
            try {
                await api.delete(`/quizzes/${quizId}`);
                // Optimistically remove from state, or refetch
                setQuizzes(quizzes.filter(q => q.id !== quizId));
            } catch (err) {
                console.error('Failed to delete quiz', err);
                alert('Failed to delete quiz');
            }
        }
    };

    const handleValidate = async (quizId) => {
        if (window.confirm('Are you sure you want to validate this quiz? This will make it public.')) {
            try {
                await api.patch(`/quizzes/${quizId}/validate`);
                // Refetch the data to get the updated status and maintain order
                fetchMyQuizzes(); 
            } catch (err) {
                console.error('Failed to validate quiz', err);
                alert('Failed to validate quiz');
            }
        }
    };

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>My Quizzes</h2>
                <Link to="/quizzes/create" className="btn btn-success">
                    + Create New Quiz
                </Link>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {loading ? (
                <div className="text-center mt-5">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            ) : (
                <div className="card shadow-sm">
                    <div className="table-responsive">
                        <table className="table table-hover mb-0">
                            <thead className="table-light">
                                <tr>
                                    <th>Title</th>
                                    <th>Category</th>
                                    <th>Status</th>
                                    <th>Questions</th>
                                    <th className="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {quizzes.length > 0 ? (
                                    quizzes.map(quiz => (
                                        <tr key={quiz.id}>
                                            <td className="align-middle fw-bold">{quiz.title}</td>
                                            <td className="align-middle">{quiz.category?.name || 'Uncategorized'}</td>
                                            <td className="align-middle">
                                                <span className={`badge ${
                                                    quiz.quizStatus === 'VALID' ? 'bg-success' : 
                                                    quiz.quizStatus === 'VALIDATABLE' ? 'bg-info' : 'bg-warning'
                                                }`}>
                                                    {quiz.quizStatus}
                                                </span>
                                            </td>
                                            <td className="align-middle">{quiz.questions?.length || 0}</td>
                                            <td className="text-end">
                                                {quiz.quizStatus === 'VALIDATABLE' && (
                                                    <button
                                                        className="btn btn-sm btn-outline-success me-2"
                                                        onClick={() => handleValidate(quiz.id)}
                                                    >
                                                        Publish
                                                    </button>
                                                )}
                                                <Link to={`/quizzes/edit/${quiz.id}`} className="btn btn-sm btn-outline-primary me-2">
                                                    Edit
                                                </Link>
                                                <button
                                                    className="btn btn-sm btn-outline-danger"
                                                    onClick={() => handleDelete(quiz.id)}
                                                >
                                                    Delete
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="5" className="text-center py-4 text-muted">
                                            You haven't created any quizzes yet.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
};

export default MyQuizzes;
