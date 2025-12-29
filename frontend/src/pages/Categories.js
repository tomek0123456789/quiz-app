import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

const Categories = () => {
    const [categories, setCategories] = useState([]);
    const [newCategoryName, setNewCategoryName] = useState('');
    const [editingId, setEditingId] = useState(null);
    const [editName, setEditName] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const fetchCategories = async () => {
        try {
            const response = await api.get('/categories');
            setCategories(response.data);
        } catch (err) {
            setError('Failed to fetch categories.');
        }
    };

    useEffect(() => {
        fetchCategories();
    }, []);

    const handleCreate = async (e) => {
        e.preventDefault();
        if (!newCategoryName.trim()) return;

        try {
            const response = await api.post('/categories', { categoryName: newCategoryName });
            setCategories([...categories, response.data]);
            setNewCategoryName('');
            setSuccess('Category created successfully.');
            setError('');
        } catch (err) {
            if (err.response && err.response.status === 409) {
                setError('Category already exists.');
            } else {
                setError('Failed to create category.');
            }
        }
    };

    const startEdit = (category) => {
        setEditingId(category.id);
        setEditName(category.name);
    };

    const cancelEdit = () => {
        setEditingId(null);
        setEditName('');
    };

    const handleUpdate = async (id) => {
        if (!editName.trim()) return;

        try {
            const response = await api.put(`/categories/${id}`, { categoryName: editName });
            
            // Backend returns the updated object or the new one if created.
            // Let's update the local list.
            setCategories(categories.map(cat => cat.id === id ? response.data : cat));
            
            setSuccess('Category updated.');
            setEditingId(null);
            setError('');
        } catch (err) {
            setError('Failed to update category.');
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure? This might affect quizzes using this category.')) return;

        try {
            await api.delete(`/categories/${id}`);
            setCategories(categories.filter(cat => cat.id !== id));
            setSuccess('Category deleted.');
        } catch (err) {
            setError('Failed to delete category.');
        }
    };

    return (
        <div className="container mt-4">
            <h2>Category Management</h2>
            {error && <div className="alert alert-danger">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            <div className="card p-3 mb-4 shadow-sm">
                <h5>Add New Category</h5>
                <form onSubmit={handleCreate} className="d-flex gap-2">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Category Name"
                        value={newCategoryName}
                        onChange={(e) => setNewCategoryName(e.target.value)}
                        required
                    />
                    <button type="submit" className="btn btn-primary">Add</button>
                </form>
            </div>

            <div className="card shadow-sm">
                <table className="table table-hover mb-0">
                    <thead className="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th className="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {categories.map(cat => (
                            <tr key={cat.id}>
                                <td className="align-middle">{cat.id}</td>
                                <td className="align-middle">
                                    {editingId === cat.id ? (
                                        <input
                                            type="text"
                                            className="form-control form-control-sm"
                                            value={editName}
                                            onChange={(e) => setEditName(e.target.value)}
                                        />
                                    ) : (
                                        cat.name
                                    )}
                                </td>
                                <td className="text-end">
                                    {editingId === cat.id ? (
                                        <>
                                            <button 
                                                className="btn btn-sm btn-success me-2"
                                                onClick={() => handleUpdate(cat.id)}
                                            >
                                                Save
                                            </button>
                                            <button 
                                                className="btn btn-sm btn-secondary"
                                                onClick={cancelEdit}
                                            >
                                                Cancel
                                            </button>
                                        </>
                                    ) : (
                                        <>
                                            <button 
                                                className="btn btn-sm btn-outline-primary me-2"
                                                onClick={() => startEdit(cat)}
                                            >
                                                Edit
                                            </button>
                                            <button 
                                                className="btn btn-sm btn-outline-danger"
                                                onClick={() => handleDelete(cat.id)}
                                            >
                                                Delete
                                            </button>
                                        </>
                                    )}
                                </td>
                            </tr>
                        ))}
                        {categories.length === 0 && (
                            <tr>
                                <td colSpan="3" className="text-center text-muted">No categories found.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Categories;
