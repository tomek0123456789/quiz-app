import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import QuizList from './pages/QuizList';
import MyQuizzes from './pages/MyQuizzes';
import CreateQuiz from './pages/CreateQuiz';
import Categories from './pages/Categories';
import RoomList from './pages/RoomList';
import RoomEditor from './pages/RoomEditor';
import RoomDetails from './pages/RoomDetails';
import TakeQuiz from './pages/TakeQuiz';
import MyResults from './pages/MyResults';
import Profile from './pages/Profile';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
            <Route element={<Layout />}>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                
                {/* Routes for all authenticated users */}
                <Route element={<ProtectedRoute />}>
                    <Route path="/" element={<Home />} />
                    <Route path="/quizzes" element={<QuizList />} />
                    <Route path="/quizzes/my" element={<MyQuizzes />} />
                    <Route path="/quizzes/create" element={<CreateQuiz />} />
                    <Route path="/quizzes/edit/:id" element={<CreateQuiz />} />
                    <Route path="/quizzes/:id" element={<TakeQuiz />} />
                    
                    <Route path="/my-rooms" element={<RoomList />} />
                    <Route path="/rooms/create" element={<RoomEditor />} />
                    <Route path="/rooms/edit/:id" element={<RoomEditor />} />
                    <Route path="/rooms/:id" element={<RoomDetails />} />
                    
                    <Route path="/results" element={<MyResults />} />
                    <Route path="/profile" element={<Profile />} />
                </Route>

                {/* Routes for ADMIN only */}
                {/* Note: Backend UserRole enum serializes to 'ADMIN', not 'ROLE_ADMIN' by default */}
                <Route element={<ProtectedRoute roles={['ADMIN', 'ROLE_ADMIN']} />}>
                    <Route path="/categories" element={<Categories />} />
                </Route>

                {/* Catch all */}
                <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;