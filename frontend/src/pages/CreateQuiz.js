import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axiosConfig';

const CreateQuiz = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [categories, setCategories] = useState([]);
    const [quizData, setQuizData] = useState({
        name: '',
        description: '',
        categoryId: ''
    });
    const [createdQuizId, setCreatedQuizId] = useState(null);
    const [questions, setQuestions] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const init = async () => {
            try {
                const catResponse = await api.get('/categories');
                setCategories(catResponse.data);

                if (id) {
                    setLoading(true);
                    const quizResponse = await api.get(`/quizzes/${id}`);
                    const quiz = quizResponse.data;
                    
                    setCreatedQuizId(quiz.id);
                    setQuizData({
                        name: quiz.title,
                        description: quiz.description,
                        categoryId: quiz.category?.id || ''
                    });

                    const mappedQuestions = (quiz.questions || []).map(q => ({
                        ...q,
                        saved: true,
                        isEditing: false,
                        answers: (q.answers || []).map(a => ({ ...a, saved: true, isEditing: false }))
                    }));
                    setQuestions(mappedQuestions);
                    setLoading(false);
                }
            } catch (err) {
                console.error("Initialization failed", err);
                setError("Failed to load data.");
                setLoading(false);
            }
        };
        init();
    }, [id]);

    const handleQuizChange = (e) => {
        setQuizData({ ...quizData, [e.target.name]: e.target.value });
    };

    const handleQuizSubmit = async (e) => {
        e.preventDefault();
        try {
            if (createdQuizId) {
                await api.patch(`/quizzes/${createdQuizId}`, {
                    title: quizData.name,
                    description: quizData.description,
                    categoryId: quizData.categoryId
                });
                setSuccess('Quiz details updated.');
            } else {
                const response = await api.post('/quizzes', quizData);
                setCreatedQuizId(response.data.id);
                setSuccess('Quiz created! Now add questions.');
            }
            setError('');
        } catch (err) {
            setError('Failed to save quiz details.');
        }
    };

    const addQuestion = () => {
        setQuestions([...questions, { 
            tempId: Date.now(), 
            content: '', 
            answers: [],
            saved: false,
            isEditing: false
        }]);
    };

    const updateQuestionContent = (index, content) => {
        const newQuestions = [...questions];
        newQuestions[index].content = content;
        setQuestions(newQuestions);
    };

    const saveQuestion = async (index) => {
        const question = questions[index];
        if (!question.content.trim()) {
            setError('Question content cannot be empty.');
            return;
        }

        try {
            // If it was already saved and we are "editing", we delete the old one first
            // (Backend doesn't have an update endpoint for questions)
            if (question.saved) {
                await api.delete(`/quizzes/${createdQuizId}/questions/${question.ordNum}`);
            }

            const qResponse = await api.post(`/quizzes/${createdQuizId}/questions`, {
                content: question.content
            });
            
            const newQuestions = [...questions];
            // Preserve answers if we were editing
            const preservedAnswers = question.answers || [];
            newQuestions[index] = { ...qResponse.data, answers: preservedAnswers, saved: true, isEditing: false };
            setQuestions(newQuestions);
            setError('');
            setSuccess('Question saved.');
        } catch (err) {
            setError('Failed to save question.');
        }
    };

    const toggleEditQuestion = (index) => {
        const newQuestions = [...questions];
        newQuestions[index].isEditing = !newQuestions[index].isEditing;
        setQuestions(newQuestions);
    };
    
    const deleteQuestion = async (index) => {
        const question = questions[index];
        if(question.saved) {
             try {
                await api.delete(`/quizzes/${createdQuizId}/questions/${question.ordNum}`);
             } catch(err) {
                 setError("Failed to delete question");
                 return;
             }
        }
        
        // Remove from list
        const remainingQuestions = questions.filter((_, i) => i !== index);
        
        // Re-index ordNums to match backend logic (1-based index)
        const reindexedQuestions = remainingQuestions.map((q, i) => ({
            ...q,
            ordNum: i + 1
        }));
        
        setQuestions(reindexedQuestions);
    };

    const addAnswer = (qIndex) => {
        const newQuestions = [...questions];
        newQuestions[qIndex].answers.push({ text: '', score: 0, saved: false, isEditing: false });
        setQuestions(newQuestions);
    };

    const updateAnswer = (qIndex, aIndex, field, value) => {
        const newQuestions = [...questions];
        newQuestions[qIndex].answers[aIndex][field] = value;
        setQuestions(newQuestions);
    };

    const saveAnswer = async (qIndex, aIndex) => {
        const question = questions[qIndex];
        const answer = question.answers[aIndex];

        if (!answer.text.trim()) {
            setError('Answer text cannot be empty.');
            return;
        }

        if (!question.saved) {
            setError('Please save the question first.');
            return;
        }

        try {
            // If editing, delete the old one first
            if (answer.saved) {
                await api.delete(`/quizzes/${createdQuizId}/questions/${question.ordNum}/answers/${answer.ordNum}`);
            }

            const response = await api.post(
                `/quizzes/${createdQuizId}/questions/${question.ordNum}/answers`, 
                { text: answer.text, score: answer.score }
            );
            
            const newQuestions = [...questions];
            newQuestions[qIndex].answers[aIndex] = { ...response.data, saved: true, isEditing: false };
            setQuestions(newQuestions);
            setError('');
            setSuccess('Answer saved.');
        } catch (err) {
            setError('Failed to save answer.');
        }
    };

    const toggleEditAnswer = (qIndex, aIndex) => {
        const newQuestions = [...questions];
        newQuestions[qIndex].answers[aIndex].isEditing = !newQuestions[qIndex].answers[aIndex].isEditing;
        setQuestions(newQuestions);
    };
    
    const deleteAnswer = async (qIndex, aIndex) => {
        const question = questions[qIndex];
        const answer = question.answers[aIndex];
        
        if(answer.saved) {
            try {
                 await api.delete(`/quizzes/${createdQuizId}/questions/${question.ordNum}/answers/${answer.ordNum}`);
            } catch(err) {
                 console.error("Failed to delete answer", err);
                 setError("Failed to delete answer");
                 return;
            }
        }
        
        const newQuestions = [...questions];
        const remainingAnswers = newQuestions[qIndex].answers.filter((_, i) => i !== aIndex);
        
        // Re-index ordNums
        newQuestions[qIndex].answers = remainingAnswers.map((a, i) => ({
            ...a,
            ordNum: i + 1
        }));
        
        setQuestions(newQuestions);
    };

    if (loading) return <div className="text-center mt-5">Loading...</div>;

    return (
        <div className="container mt-4 mb-5">
            <h2>{id ? 'Edit Quiz' : 'Create New Quiz'}</h2>
            {error && <div className="alert alert-danger sticky-top mt-2 shadow-sm">{error}</div>}
            {success && <div className="alert alert-success mt-2 shadow-sm">{success}</div>}

            <form onSubmit={handleQuizSubmit} className="card p-4 shadow-sm mb-4">
                <div className="mb-3">
                    <label className="form-label">Quiz Title</label>
                    <input type="text" className="form-control" name="name" value={quizData.name} onChange={handleQuizChange} required />
                </div>
                <div className="mb-3">
                    <label className="form-label">Description</label>
                    <textarea className="form-control" name="description" value={quizData.description} onChange={handleQuizChange} required />
                </div>
                <div className="mb-3">
                    <label className="form-label">Category</label>
                    <select className="form-select" name="categoryId" value={quizData.categoryId} onChange={handleQuizChange} required >
                        <option value="">Select Category</option>
                        {categories.map(cat => (<option key={cat.id} value={cat.id}>{cat.name}</option>))}
                    </select>
                </div>
                <button type="submit" className="btn btn-primary">{id ? 'Update Details' : 'Create Quiz & Continue'}</button>
            </form>

            {createdQuizId && (
                <div>
                    <hr />
                    <h3>Questions Management</h3>
                    {questions.map((q, qIndex) => (
                        <div key={q.tempId || q.id} className="card mb-3 shadow-sm border-secondary">
                            <div className="card-body">
                                <div className="d-flex justify-content-between align-items-center mb-3">
                                    <h5 className="text-secondary">Question {qIndex + 1} {q.saved && <span className="badge bg-success ms-2">Saved</span>}</h5>
                                    <div>
                                        {q.saved && !q.isEditing ? (
                                            <button className="btn btn-outline-primary btn-sm me-2" onClick={() => toggleEditQuestion(qIndex)}>Edit Content</button>
                                        ) : (
                                            <button className="btn btn-success btn-sm me-2" onClick={() => saveQuestion(qIndex)}>{q.saved ? 'Update' : 'Save'}</button>
                                        )}
                                        <button className="btn btn-danger btn-sm" onClick={() => deleteQuestion(qIndex)}>Delete</button>
                                    </div>
                                </div>

                                <input
                                    type="text"
                                    className="form-control mb-3"
                                    placeholder="Enter question content"
                                    value={q.content}
                                    onChange={(e) => updateQuestionContent(qIndex, e.target.value)}
                                    disabled={q.saved && !q.isEditing}
                                />

                                {q.saved && (
                                    <div className="ms-4 bg-light p-3 rounded">
                                        <h6>Answers</h6>
                                        {q.answers.map((a, aIndex) => (
                                            <div key={aIndex} className="d-flex gap-2 mb-2 align-items-center">
                                                <input
                                                    type="text"
                                                    className="form-control"
                                                    placeholder="Answer text"
                                                    value={a.text}
                                                    onChange={(e) => updateAnswer(qIndex, aIndex, 'text', e.target.value)}
                                                    disabled={a.saved && !a.isEditing}
                                                />
                                                <input
                                                    type="number"
                                                    className="form-control"
                                                    style={{ width: '80px' }}
                                                    value={a.score}
                                                    onChange={(e) => updateAnswer(qIndex, aIndex, 'score', parseInt(e.target.value))}
                                                    disabled={a.saved && !a.isEditing}
                                                />
                                                {a.saved && !a.isEditing ? (
                                                    <button className="btn btn-sm btn-outline-primary" onClick={() => toggleEditAnswer(qIndex, aIndex)}>Edit</button>
                                                ) : (
                                                    <button className="btn btn-sm btn-success" onClick={() => saveAnswer(qIndex, aIndex)}>{a.saved ? 'Update' : 'Save'}</button>
                                                )}
                                                <button className="btn btn-sm btn-outline-danger" onClick={() => deleteAnswer(qIndex, aIndex)}>X</button>
                                            </div>
                                        ))}
                                        <button className="btn btn-sm btn-link" onClick={() => addAnswer(qIndex)}>+ Add Answer</button>
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                    <button className="btn btn-outline-primary w-100 py-2 mb-4" onClick={addQuestion}>+ Add New Question</button>
                    <button className="btn btn-success w-100 py-3" onClick={() => navigate('/quizzes/my')}>Finish & Back to My Quizzes</button>
                </div>
            )}
        </div>
    );
};

export default CreateQuiz;
