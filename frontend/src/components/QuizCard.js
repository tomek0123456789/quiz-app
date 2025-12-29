import React from 'react';
import { Link } from 'react-router-dom';

const QuizCard = ({ quiz }) => {
    return (
        <div className="card h-100 shadow-sm">
            <div className="card-body d-flex flex-column">
                <h5 className="card-title">{quiz.title}</h5>
                <h6 className="card-subtitle mb-2 text-muted">
                    {quiz.category?.name || 'Uncategorized'}
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
