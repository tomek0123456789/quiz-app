import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

const MyResults = () => {
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchResults = async () => {
            try {
                const response = await api.get('/results/my');
                setResults(response.data);
            } catch (err) {
                console.error("Failed to fetch results", err);
                setError("Failed to load results.");
            } finally {
                setLoading(false);
            }
        };
        fetchResults();
    }, []);

    if (loading) return <div className="text-center mt-5">Loading...</div>;

    return (
        <div className="container mt-4">
            <h2 className="mb-4">My Results</h2>
            {error && <div className="alert alert-danger">{error}</div>}

            <div className="card shadow-sm">
                <div className="table-responsive">
                    <table className="table table-hover mb-0">
                        <thead className="table-light">
                            <tr>
                                <th>Date</th>
                                <th>Room</th>
                                <th>Quiz name</th>
                                <th>Total Score</th>
                            </tr>
                        </thead>
                        <tbody>
                            {results.length > 0 ? (
                                results.map(result => (
                                    <tr key={result.id}>
                                        <td className="align-middle">{new Date(result.createdAt).toLocaleString()}</td>
                                        <td className="align-middle">{result.room ? result.room.roomName : <span className="text-muted">Solo Play</span>}</td>
                                        <td>
                                            {result.quizzesResults.map(qr => (
                                                <div key={qr.id} className="d-flex justify-content-between align-items-center py-1">
                                                    <span>
                                                        <i className="bi bi-file-text me-2"></i>
                                                        {qr.quiz?.title || 'Unknown Quiz'}
                                                    </span>
                                                </div>
                                            ))}
                                        </td>
                                        <td className="align-middle fw-bold fs-5 text-center">{result.score}</td>

                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="4" className="text-center py-4 text-muted">
                                        No results found. Go take some quizzes!
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default MyResults;
