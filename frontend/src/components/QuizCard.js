import React from 'react';
import { Link } from 'react-router-dom';

const QuizCard = ({ quiz, onDelete, isAdmin }) => {
    const getBorderClass = () => {
        if (!isAdmin) return '';
        switch (quiz.quizStatus) {
            case 'VALID': return 'border-success border-2';
            case 'VALIDATABLE': return 'border-info border-2';
            case 'INVALID': return 'border-warning border-2';
            default: return '';
        }
    };

    return (
        <div className={`card h-100 shadow-sm ${getBorderClass()}`}>
            <div className="card-body d-flex flex-column">
                <div className="d-flex justify-content-between align-items-start mb-2">
                    <h5 className="card-title mb-0">{quiz.title}</h5>
                    {onDelete && (
                        <button 
                            className="btn btn-sm btn-outline-danger ms-2"
                            onClick={onDelete}
                            title="Delete Quiz"
                        >
                            Delete
                        </button>
                    )}
                </div>
                <h6 className="card-subtitle mb-2 text-muted">
                    {quiz.category?.name || 'Uncategorized'}
                    {isAdmin && (
                        <span className="ms-2 badge bg-light text-dark border">
                            {quiz.quizStatus}
                        </span>
                    )}
                </h6>
                <p className="card-text text-truncate">{quiz.description}</p>
                <div className="mt-auto">
                    <Link to={`/quizzes/${quiz.id}`} className="btn btn-primary w-100">
                        Take Quiz
                    </Link>
                </div>
            </div>
            { /*
            <div className="card-footer text-muted small">
                By {quiz.owner?.name || 'Unknown'}
            </div>
                */
            }
        </div>
    );
};

export default QuizCard;
