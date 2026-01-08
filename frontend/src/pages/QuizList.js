import React, { useState, useEffect, useCallback } from 'react';
import api from '../api/axiosConfig';
import QuizCard from '../components/QuizCard';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const QuizList = () => {
    const [quizzes, setQuizzes] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const { hasRole } = useAuth();

    const isAdmin = hasRole('ADMIN') || hasRole('ROLE_ADMIN');

    const [filters, setFilters] = useState({
        name: '',
        category: ''
    });

    const fetchCategories = async () => {
        try {
            const response = await api.get('/categories');
            setCategories(response.data);
        } catch (err) {
            console.error('Error fetching categories:', err);
        }
    };

    const fetchQuizzes = useCallback(async () => {
        setLoading(true);
        try {
            const params = {};
            if (filters.name) params.name = filters.name;
            if (filters.category) params.category = filters.category;

            const response = await api.get('/quizzes', { params });
            setQuizzes(response.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch quizzes');
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [filters]);

    useEffect(() => {
        fetchCategories();
    }, []);

    useEffect(() => {
        const timeoutId = setTimeout(() => {
            fetchQuizzes();
        }, 300); // Debounce search
        return () => clearTimeout(timeoutId);
    }, [fetchQuizzes]);

    const handleFilterChange = (e) => {
        setFilters({
            ...filters,
            [e.target.name]: e.target.value
        });
    };

    const handleDeleteQuiz = async (quizId) => {
        if (!window.confirm('Are you sure you want to delete this quiz?')) return;
        try {
            await api.delete(`/quizzes/${quizId}`);
            setQuizzes(quizzes.filter(q => q.id !== quizId));
        } catch (err) {
            console.error('Failed to delete quiz', err);
            alert('Failed to delete quiz.');
        }
    };

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Browse Quizzes</h2>
                <Link to="/quizzes/create" className="btn btn-success">
                    + Create New Quiz
                </Link>
            </div>

            <div className="card p-3 mb-4 bg-light">
                <div className="d-flex gap-2">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Search by title..."
                        name="name"
                        value={filters.name}
                        onChange={handleFilterChange}
                    />
                    <select
                        className="form-select"
                        name="category"
                        value={filters.category}
                        onChange={handleFilterChange}
                    >
                        <option value="">All Categories</option>
                        {categories.map(cat => (
                            <option key={cat.id} value={cat.name}>{cat.name}</option>
                        ))}
                    </select>
                </div>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {loading ? (
                <div className="text-center mt-5">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            ) : (
                <div className="row row-cols-1 row-cols-md-3 g-4">
                    {quizzes.length > 0 ? (
                        quizzes.map(quiz => (
                            <div className="col" key={quiz.id}>
                                <QuizCard 
                                    quiz={quiz} 
                                    isAdmin={isAdmin}
                                    onDelete={isAdmin ? () => handleDeleteQuiz(quiz.id) : null}
                                />
                            </div>
                        ))
                    ) : (
                        <div className="col-12 text-center mt-5">
                            <p className="text-muted">No quizzes found.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default QuizList;

