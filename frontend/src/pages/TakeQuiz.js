import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const TakeQuiz = () => {
    const { id } = useParams();
    const [searchParams] = useSearchParams();
    const roomId = searchParams.get('roomId');
    const navigate = useNavigate();

    const [quiz, setQuiz] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    
    // State to store user answers: { [questionOrdNum]: answerOrdNum }
    const [userAnswers, setUserAnswers] = useState({});
    
    // Game state
    const [started, setStarted] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        const fetchQuiz = async () => {
            try {
                const response = await api.get(`/quizzes/${id}`);
                setQuiz(response.data);
            } catch (err) {
                console.error("Failed to fetch quiz", err);
                setError("Failed to load quiz.");
            } finally {
                setLoading(false);
            }
        };
        fetchQuiz();
    }, [id]);

    const handleAnswerSelect = (questionOrdNum, answerOrdNum) => {
        setUserAnswers({
            ...userAnswers,
            [questionOrdNum]: answerOrdNum
        });
    };

    const handleSubmit = async () => {
        if (!window.confirm("Are you sure you want to submit?")) return;
        setSubmitting(true);

        try {
            // Construct the complex ResultsDto structure
            // ResultsDto -> quizzesResults (Set) -> questionsAndAnswers (Set)
            
            const questionsAndAnswers = Object.entries(userAnswers).map(([qOrd, aOrd]) => ({
                questionOrdNum: parseInt(qOrd),
                userAnswerOrdNum: parseInt(aOrd)
            }));

            const quizResult = {
                quizId: parseInt(id),
                questionsAndAnswers: questionsAndAnswers
            };

            const payload = {
                quizzesResults: [quizResult] // Set<QuizResultsModel>
            };

            if (roomId) {
                await api.post(`/myrooms/${roomId}/results`, payload);
                alert("Results submitted to Room!");
                navigate(`/rooms/${roomId}`);
            } else {
                await api.post('/results', payload);
                alert("Results submitted!");
                navigate('/results');
            }

        } catch (err) {
            console.error("Submission failed", err);
            alert("Failed to submit results. " + (err.response?.data?.message || ""));
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="text-center mt-5">Loading...</div>;
    if (error) return <div className="alert alert-danger mt-4">{error}</div>;
    if (!quiz) return <div className="alert alert-warning mt-4">Quiz not found</div>;

    if (!started) {
        return (
            <div className="container mt-5 text-center">
                <div className="card shadow-lg p-5">
                    <h1 className="mb-3">{quiz.title}</h1>
                    <p className="lead text-muted">{quiz.description}</p>
                    <hr />
                    <div className="text-start mb-4">
                        <p><strong>Category:</strong> {quiz.category?.name}</p>
                        <p><strong>Questions:</strong> {quiz.questions?.length}</p>
                        {roomId && <div className="alert alert-info">You are taking this quiz as part of a Room context.</div>}
                    </div>
                    <button className="btn btn-primary btn-lg px-5" onClick={() => setStarted(true)}>
                        Start Quiz
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-4 mb-5">
            <h3 className="mb-4">{quiz.title} <small className="text-muted fs-6">({quiz.questions.length} Questions)</small></h3>
            
            {quiz.questions.map((q, index) => (
                <div key={q.id} className="card mb-4 shadow-sm">
                    <div className="card-header bg-white fw-bold">
                        Q{index + 1}. {q.content}
                    </div>
                    <div className="card-body">
                        <div className="list-group">
                            {q.answers.map((a) => (
                                <button
                                    key={a.id}
                                    type="button"
                                    className={`list-group-item list-group-item-action ${
                                        userAnswers[q.ordNum] === a.ordNum ? 'active' : ''
                                    }`}
                                    onClick={() => handleAnswerSelect(q.ordNum, a.ordNum)}
                                >
                                    {a.text}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            ))}

            <div className="d-grid gap-2">
                <button 
                    className="btn btn-success btn-lg" 
                    onClick={handleSubmit} 
                    disabled={submitting}
                >
                    {submitting ? 'Submitting...' : 'Submit Answers'}
                </button>
            </div>
        </div>
    );
};

export default TakeQuiz;
